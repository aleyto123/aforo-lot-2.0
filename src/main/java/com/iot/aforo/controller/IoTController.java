package com.iot.aforo.controller;

import com.iot.aforo.model.Business;
import com.iot.aforo.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iot")
@CrossOrigin(origins = "*")
public class IoTController {

    private final BusinessService businessService;

    @Autowired
    public IoTController(BusinessService businessService) {
        this.businessService = businessService;
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
}
