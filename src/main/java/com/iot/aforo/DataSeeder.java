package com.iot.aforo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.iot.aforo.model.AforoRegistro;
import com.iot.aforo.model.Business;
import com.iot.aforo.model.TipoRegistro;
import com.iot.aforo.model.User;
import com.iot.aforo.repository.AforoRegistroRepository;
import com.iot.aforo.repository.BusinessRepository;
import com.iot.aforo.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AforoRegistroRepository aforoRegistroRepository;

    @Autowired
    public DataSeeder(UserRepository userRepository,
                      BusinessRepository businessRepository,
                      AforoRegistroRepository aforoRegistroRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.aforoRegistroRepository = aforoRegistroRepository;
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

        // Crear negocio principal del owner si no existe.
        Business business = businessRepository.findById(1L).orElseGet(() -> {
            Business created = new Business("owner", "RETAIL", "LIMA", 25);
            return businessRepository.save(created);
        });

        if (userRepository.findByUsername("owner").isEmpty()) {
            User owner = new User("owner", "owner123", "USER", business.getId(), 3);
            userRepository.save(owner);
            System.out.println(">>> Sembrador: Dueño de negocio creado por defecto (owner / owner123)");
        }

        if (aforoRegistroRepository.count() == 0) {
            seedHistoricEntries(business.getId());
            System.out.println(">>> Sembrador: Historial de 3 meses generado para el negocio " + business.getId());
        }
    }

    private void seedHistoricEntries(Long businessId) {
        Random random = new Random(42L);
        List<AforoRegistro> entries = new ArrayList<>();

        for (int offset = 2; offset >= 0; offset--) {
            LocalDate current = LocalDate.now().minusMonths(offset);
            int daysInMonth = current.lengthOfMonth();

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = current.withDayOfMonth(day);
                int baseTraffic = switch (date.getDayOfWeek()) {
                    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY -> 8 + random.nextInt(5);
                    case FRIDAY -> 12 + random.nextInt(7);
                    case SATURDAY -> 16 + random.nextInt(10);
                    default -> 5 + random.nextInt(4);
                };

                for (int hour = 8; hour < 22; hour++) {
                    int trafficAtHour = baseTraffic + (hour >= 12 && hour <= 18 ? 4 : 0) + (hour >= 18 && hour <= 21 ? 3 : 0);
                    int visits = Math.max(1, trafficAtHour + random.nextInt(6) - 2);
                    int exitCount = Math.max(0, Math.min(visits, 2 + random.nextInt(4)));
                    int entryCount = Math.max(1, visits - exitCount);

                    for (int i = 0; i < entryCount; i++) {
                        LocalDateTime entryTime = LocalDateTime.of(date, LocalTime.of(hour, 5 + random.nextInt(50)));
                        AforoRegistro entry = new AforoRegistro(businessId, TipoRegistro.ENTRADA);
                        entry.setFechaHora(entryTime);
                        entries.add(entry);
                    }

                    for (int i = 0; i < exitCount; i++) {
                        LocalDateTime exitTime = LocalDateTime.of(date, LocalTime.of(hour, 10 + random.nextInt(50)));
                        AforoRegistro exit = new AforoRegistro(businessId, TipoRegistro.SALIDA);
                        exit.setFechaHora(exitTime);
                        entries.add(exit);
                    }
                }
            }
        }

        aforoRegistroRepository.saveAll(entries);
    }
}
