package com.iot.aforo.controller;

import com.iot.aforo.model.AforoRegistro;
import com.iot.aforo.model.Alert;
import com.iot.aforo.model.Business;
import com.iot.aforo.service.AforoRegistroService;
import com.iot.aforo.service.AlertService;
import com.iot.aforo.service.BusinessService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iot")
@CrossOrigin(origins = "*")
public class IoTController {

    private final BusinessService businessService;
    private final AlertService alertService;
    private final AforoRegistroService aforoRegistroService;

    @Autowired
    public IoTController(BusinessService businessService, AlertService alertService, AforoRegistroService aforoRegistroService) {
        this.businessService = businessService;
        this.alertService = alertService;
        this.aforoRegistroService = aforoRegistroService;
    }

    // Endpoint para cuando una persona entra
    @PostMapping("/business/{id}/enter")
    public ResponseEntity<Business> personEntered(@PathVariable Long id) {
        try {
            Business business = businessService.registerEntry(id);
            return ResponseEntity.ok(business);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para cuando una persona sale
    @PostMapping("/business/{id}/exit")
    public ResponseEntity<Business> personExited(@PathVariable Long id) {
        try {
            Business business = businessService.registerExit(id);
            return ResponseEntity.ok(business);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Alertas activas no resueltas para polling del frontend IoT.
    @GetMapping("/business/{id}/alerts")
    public ResponseEntity<List<Alert>> getUnreadAlerts(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getActiveAlertsForBusiness(id));
    }

    // Historial cronologico descendente de entradas y salidas.
    @GetMapping("/business/{id}/history")
    public ResponseEntity<List<AforoRegistro>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(aforoRegistroService.listarHistorial(id));
    }

    // Volumen de ingresos por dia del mes actual para Chart.js.
    @GetMapping("/business/{id}/stats/monthly-entries")
    public ResponseEntity<Map<Integer, Long>> getMonthlyEntries(@PathVariable Long id) {
        return ResponseEntity.ok(aforoRegistroService.obtenerEntradasDelMes(id));
    }
}
