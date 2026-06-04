package com.iot.aforo;

import com.iot.aforo.model.Business;
import com.iot.aforo.model.User;
import com.iot.aforo.repository.BusinessRepository;
import com.iot.aforo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    @Autowired
    public DataSeeder(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear cuenta maestra MASTER_ADMIN
        String masterEmail = "aleyto";
        if (userRepository.findByUsername(masterEmail).isEmpty()) {
            User masterAdmin = new User(masterEmail, "123", "MASTER_ADMIN", null);
            userRepository.save(masterAdmin);
            System.out.println(">>> Sembrador: Cuenta Maestra MASTER_ADMIN creada (" + masterEmail + ")");
        }

        // Crear negocio de prueba y su dueño si no existen
        if (businessRepository.count() == 0) {
            Business business = new Business("Tienda de Ropa Ronyc", "BODEGA", "LIMA", 10);
            business = businessRepository.save(business);
            System.out.println(">>> Sembrador: Negocio de prueba creado: " + business.getName() + " (ID: " + business.getId() + ")");

            if (userRepository.findByUsername("owner").isEmpty()) {
                User owner = new User("owner", "owner123", "USER", business.getId(), 3);
                userRepository.save(owner);
                System.out.println(">>> Sembrador: Dueño de negocio creado por defecto (owner / owner123)");
            }
        }
    }
}
