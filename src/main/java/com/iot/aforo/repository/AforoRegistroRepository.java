package com.iot.aforo.repository;

import com.iot.aforo.model.AforoRegistro;
import com.iot.aforo.model.TipoRegistro;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AforoRegistroRepository extends JpaRepository<AforoRegistro, Long> {

    List<AforoRegistro> findByBusinessIdOrderByFechaHoraDesc(Long businessId);

    @Query("""
            select day(r.fechaHora), count(r)
            from AforoRegistro r
            where r.businessId = :businessId
              and r.tipo = :tipo
              and r.fechaHora >= :inicio
              and r.fechaHora < :fin
            group by day(r.fechaHora)
            order by day(r.fechaHora)
            """)
    List<Object[]> countByDayForMonth(Long businessId, TipoRegistro tipo, LocalDateTime inicio, LocalDateTime fin);
}
