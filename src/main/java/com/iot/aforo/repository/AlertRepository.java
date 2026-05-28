package com.iot.aforo.repository;

import com.iot.aforo.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByBusinessId(Long businessId);
    List<Alert> findByBusinessIdAndResolvedFalse(Long businessId);
}
