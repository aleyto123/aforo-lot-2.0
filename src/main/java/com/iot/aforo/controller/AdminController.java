package com.iot.aforo.controller;

import com.iot.aforo.model.Business;
import com.iot.aforo.model.User;
import com.iot.aforo.service.BusinessService;
import com.iot.aforo.service.UserService;
import com.iot.aforo.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserService userService;
    private final BusinessService businessService;
    private final BusinessRepository businessRepository; // Para listar fácilmente los negocios

    @Autowired
    public AdminController(UserService userService, BusinessService businessService, BusinessRepository businessRepository) {
        this.userService = userService;
        this.businessService = businessService;
        this.businessRepository = businessRepository;
    }

    // Listar todos los negocios
    @GetMapping("/businesses")
    public ResponseEntity<List<Business>> getAllBusinesses() {
        return ResponseEntity.ok(businessRepository.findAll());
    }

    // Crear un nuevo negocio
    @PostMapping("/businesses")
    public ResponseEntity<Business> createBusiness(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String category = (String) body.get("category");
        String location = (String) body.get("location");
        int maxCapacity = toInt(body.get("maxCapacity"), 1);
        
        Business business = businessService.createBusiness(name, category, location, maxCapacity);
        return ResponseEntity.ok(business);
    }

    // Configurar el aforo maximo operativo de un negocio
    @PutMapping("/businesses/{id}/config")
    public ResponseEntity<Business> updateConfig(@PathVariable Long id, @RequestBody Map<String, Integer> config) {
        try {
            int maxCapacity = config.get("maxCapacity");
            Business business = businessService.updateConfig(id, maxCapacity);
            return ResponseEntity.ok(business);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Crear un usuario común o administrador subordinado
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body, @RequestHeader(value = "X-Creator-Id", required = false) Long creatorId) {
        try {
            if (creatorId == null) {
                return ResponseEntity.status(401).body("Acceso no autorizado. Debe identificarse para crear usuarios.");
            }
            
            User creator = userService.findById(creatorId).orElse(null);
            if (creator == null) {
                return ResponseEntity.status(401).body("Creador no encontrado.");
            }
            
            String roleRequested = body.get("role"); // "USER" por defecto
            if (roleRequested == null) {
                roleRequested = "USER";
            }
            
            // Reglas de negocio:
            // 1. Un usuario común (USER) no puede crear a nadie.
            if ("USER".equals(creator.getRole())) {
                return ResponseEntity.status(403).body("Los dueños de negocio no tienen permiso para crear cuentas.");
            }
            
            // 2. Solo el MASTER_ADMIN puede crear cuentas de tipo ADMIN.
            if ("ADMIN".equals(roleRequested) && !"MASTER_ADMIN".equals(creator.getRole())) {
                return ResponseEntity.status(403).body("Solo la cuenta maestra (Master Admin) puede crear otros administradores.");
            }
            
            // 3. Nadie puede crear otro MASTER_ADMIN.
            if ("MASTER_ADMIN".equals(roleRequested)) {
                return ResponseEntity.status(403).body("No se puede crear otra cuenta maestra.");
            }
            
            String username = body.get("username");
            String password = body.get("password");
            String businessIdStr = body.get("businessId");
            String minCapacityPeopleStr = body.get("minCapacityPeople");
            
            Long businessId = null;
            if (businessIdStr != null && !businessIdStr.isEmpty()) {
                businessId = Long.parseLong(businessIdStr);
            }

            int minCapacityPeople = 0;
            if (minCapacityPeopleStr != null && !minCapacityPeopleStr.isBlank()) {
                minCapacityPeople = Integer.parseInt(minCapacityPeopleStr);
            }
            
            User user = userService.createUser(username, password, roleRequested, businessId, minCapacityPeople);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear usuario: " + e.getMessage());
        }
    }

    private int toInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            return Integer.parseInt(text);
        }
        return defaultValue;
    }
}
