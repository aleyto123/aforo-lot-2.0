package com.iot.aforo.service;

import com.iot.aforo.model.AlertaAforo;
import com.iot.aforo.repository.AlertaAforoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaAforoService {

    private final AlertaAforoRepository alertaAforoRepository;

    public AlertaAforoService(AlertaAforoRepository alertaAforoRepository) {
        this.alertaAforoRepository = alertaAforoRepository;
    }

    @Transactional
    public AlertaAforo crearAlerta(Long businessId, String mensaje) {
        return alertaAforoRepository.save(new AlertaAforo(businessId, mensaje));
    }

    public List<AlertaAforo> listarNoLeidas(Long businessId) {
        return alertaAforoRepository.findByBusinessIdAndLeidaFalseOrderByFechaHoraDesc(businessId);
    }

    public List<AlertaAforo> listarTodas(Long businessId) {
        return alertaAforoRepository.findByBusinessIdOrderByFechaHoraDesc(businessId);
    }

    @Transactional
    public AlertaAforo marcarComoLeida(Long id) {
        return alertaAforoRepository.findById(id)
                .map(alerta -> {
                    alerta.setLeida(true);
                    return alertaAforoRepository.save(alerta);
                })
                .orElseThrow(() -> new IllegalArgumentException("Alerta no encontrada con id: " + id));
    }
}
