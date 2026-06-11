package com.iot.aforo.repository;

import com.iot.aforo.model.AlertaAforo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertaAforoRepository extends JpaRepository<AlertaAforo, Long> {

    List<AlertaAforo> findByBusinessIdAndLeidaFalseOrderByFechaHoraDesc(Long businessId);

    List<AlertaAforo> findByBusinessIdOrderByFechaHoraDesc(Long businessId);
}
