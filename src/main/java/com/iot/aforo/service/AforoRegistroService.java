package com.iot.aforo.service;

import com.iot.aforo.model.AforoRegistro;
import com.iot.aforo.model.TipoRegistro;
import com.iot.aforo.repository.AforoRegistroRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AforoRegistroService {

    private final AforoRegistroRepository aforoRegistroRepository;

    @Autowired
    public AforoRegistroService(AforoRegistroRepository aforoRegistroRepository) {
        this.aforoRegistroRepository = aforoRegistroRepository;
    }

    public AforoRegistro registrarMovimiento(Long businessId, TipoRegistro tipo) {
        return aforoRegistroRepository.save(new AforoRegistro(businessId, tipo));
    }

    public List<AforoRegistro> listarHistorial(Long businessId) {
        return aforoRegistroRepository.findByBusinessIdOrderByFechaHoraDesc(businessId);
    }

    public Map<Integer, Long> obtenerEntradasDelMes(Long businessId) {
        YearMonth mesActual = YearMonth.from(LocalDate.now());
        LocalDateTime inicio = mesActual.atDay(1).atStartOfDay();
        LocalDateTime fin = mesActual.plusMonths(1).atDay(1).atStartOfDay();
        Map<Integer, Long> ingresosPorDia = new LinkedHashMap<>();

        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            ingresosPorDia.put(dia, 0L);
        }

        List<Object[]> resultados = aforoRegistroRepository.countByDayForMonth(
                businessId,
                TipoRegistro.ENTRADA,
                inicio,
                fin
        );

        for (Object[] fila : resultados) {
            Integer dia = ((Number) fila[0]).intValue();
            Long total = ((Number) fila[1]).longValue();
            ingresosPorDia.put(dia, total);
        }

        return ingresosPorDia;
    }
}
