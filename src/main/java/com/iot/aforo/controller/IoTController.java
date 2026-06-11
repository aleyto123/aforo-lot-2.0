package com.iot.aforo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.aforo.model.AforoRegistro;
import com.iot.aforo.model.AlertaAforo;
import com.iot.aforo.model.Business;
import com.iot.aforo.service.AforoRegistroService;
import com.iot.aforo.service.AlertaAforoService;
import com.iot.aforo.service.BusinessService;

@RestController
@RequestMapping("/api/iot")
@CrossOrigin(origins = "*")
public class IoTController {

    private final BusinessService businessService;
    private final AlertaAforoService alertaAforoService;
    private final AforoRegistroService aforoRegistroService;

    @Autowired
    public IoTController(BusinessService businessService,
                         AlertaAforoService alertaAforoService,
                         AforoRegistroService aforoRegistroService) {
        this.businessService = businessService;
        this.alertaAforoService = alertaAforoService;
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
    public ResponseEntity<List<AlertaAforo>> getUnreadAlerts(@PathVariable Long id) {
        return ResponseEntity.ok(alertaAforoService.listarNoLeidas(id));
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
