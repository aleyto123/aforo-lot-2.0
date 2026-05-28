package com.iot.aforo.service;

import com.iot.aforo.model.Business;
import com.iot.aforo.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final AlertService alertService;

    @Autowired
    public BusinessService(BusinessRepository businessRepository, AlertService alertService) {
        this.businessRepository = businessRepository;
        this.alertService = alertService;
    }

    public Business getBusiness(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Negocio no encontrado con id: " + id));
    }

    public Business createBusiness(String name, int maxCapacity, int minCapacityProfit) {
        Business business = new Business(name, maxCapacity, minCapacityProfit);
        return businessRepository.save(business);
    }

    @Transactional
    public Business registerEntry(Long businessId) {
        Business business = getBusiness(businessId);
        business.incrementEntries();
        
        // Comprobar si se superó el límite de aforo
        if (business.getCurrentCount() > business.getMaxCapacity()) {
            String alertMsg = String.format("¡ALERTA DE CAPACIDAD! El negocio '%s' ha superado el aforo máximo. Personas adentro: %d, Capacidad límite: %d.", 
                    business.getName(), business.getCurrentCount(), business.getMaxCapacity());
            alertService.createAlert(businessId, alertMsg);
        }
        
        return businessRepository.save(business);
    }

    @Transactional
    public Business registerExit(Long businessId) {
        Business business = getBusiness(businessId);
        business.incrementExits();
        return businessRepository.save(business);
    }

    @Transactional
    public Business updateConfig(Long businessId, int maxCapacity, int minCapacityProfit) {
        Business business = getBusiness(businessId);
        business.setMaxCapacity(maxCapacity);
        business.setMinCapacityProfit(minCapacityProfit);
        return businessRepository.save(business);
    }

    @Transactional
    public boolean performEndOfDayCheck(Long businessId) {
        Business business = getBusiness(businessId);
        boolean match = business.getTotalEntriesToday() == business.getTotalExitsToday();
        
        if (!match) {
            String alertMsg = String.format("¡ALERTA DE FIN DE DÍA! Discrepancia detectada en el conteo diario del negocio '%s'. Entradas registradas: %d, Salidas registradas: %d.", 
                    business.getName(), business.getTotalEntriesToday(), business.getTotalExitsToday());
            alertService.createAlert(businessId, alertMsg);
        }
        
        return match;
    }

    @Transactional
    public void resetDailyCounts(Long businessId) {
        Business business = getBusiness(businessId);
        business.setCurrentCount(0);
        business.setTotalEntriesToday(0);
        business.setTotalExitsToday(0);
        businessRepository.save(business);
    }
}
