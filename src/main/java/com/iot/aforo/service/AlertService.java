package com.iot.aforo.service;

import com.iot.aforo.model.Alert;
import com.iot.aforo.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    @Autowired
    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert createAlert(Long businessId, String message) {
        Alert alert = new Alert(businessId, message);
        return alertRepository.save(alert);
    }

    public List<Alert> getAlertsForBusiness(Long businessId) {
        return alertRepository.findByBusinessId(businessId);
    }

    public List<Alert> getActiveAlertsForBusiness(Long businessId) {
        return alertRepository.findByBusinessIdAndResolvedFalse(businessId);
    }

    public Alert resolveAlert(Long alertId) {
        return alertRepository.findById(alertId)
                .map(alert -> {
                    alert.setResolved(true);
                    return alertRepository.save(alert);
                })
                .orElseThrow(() -> new IllegalArgumentException("Alerta no encontrada con id: " + alertId));
    }
}
