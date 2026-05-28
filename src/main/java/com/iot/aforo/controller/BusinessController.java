package com.iot.aforo.controller;

import com.iot.aforo.model.Alert;
import com.iot.aforo.model.Business;
import com.iot.aforo.service.AlertService;
import com.iot.aforo.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/business")
@CrossOrigin(origins = "*")
public class BusinessController {

    private final BusinessService businessService;
    private final AlertService alertService;

    @Autowired
    public BusinessController(BusinessService businessService, AlertService alertService) {
        this.businessService = businessService;
        this.alertService = alertService;
    }

    // Obtener estado actual del negocio (aforo y rentabilidad)
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getBusinessStatus(@PathVariable Long id) {
        try {
            Business business = businessService.getBusiness(id);
            boolean meetsProfitability = business.getCurrentCount() >= business.getMinCapacityProfit();
            boolean isFull = business.getCurrentCount() >= business.getMaxCapacity();
            
            Map<String, Object> status = new HashMap<>();
            status.put("businessId", business.getId());
            status.put("name", business.getName());
            status.put("currentCount", business.getCurrentCount());
            status.put("maxCapacity", business.getMaxCapacity());
            status.put("minCapacityProfit", business.getMinCapacityProfit());
            status.put("totalEntriesToday", business.getTotalEntriesToday());
            status.put("totalExitsToday", business.getTotalExitsToday());
            status.put("meetsProfitability", meetsProfitability);
            status.put("isFull", isFull);
            
            return ResponseEntity.ok(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Ejecutar la verificación de fin de día
    @PostMapping("/{id}/end-of-day")
    public ResponseEntity<?> endOfDayCheck(@PathVariable Long id) {
        try {
            boolean match = businessService.performEndOfDayCheck(id);
            Business business = businessService.getBusiness(id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("match", match);
            result.put("totalEntriesToday", business.getTotalEntriesToday());
            result.put("totalExitsToday", business.getTotalExitsToday());
            if (match) {
                result.put("message", "El conteo de entradas y salidas coincide perfectamente. ¡Buen trabajo!");
            } else {
                result.put("message", "Discrepancia detectada. Se ha generado una alerta para revisión.");
            }
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Listar alertas activas del negocio
    @GetMapping("/{id}/alerts")
    public ResponseEntity<List<Alert>> getActiveAlerts(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getActiveAlertsForBusiness(id));
    }

    // Resolver una alerta
    @PutMapping("/{id}/alerts/{alertId}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable Long id, @PathVariable Long alertId) {
        try {
            Alert alert = alertService.resolveAlert(alertId);
            return ResponseEntity.ok(alert);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Reiniciar los conteos del día
    @PostMapping("/{id}/reset")
    public ResponseEntity<?> resetCounts(@PathVariable Long id) {
        try {
            businessService.resetDailyCounts(id);
            return ResponseEntity.ok("Conteos diarios reiniciados con éxito.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
