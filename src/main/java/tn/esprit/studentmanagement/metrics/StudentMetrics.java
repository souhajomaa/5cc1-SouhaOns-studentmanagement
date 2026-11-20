package tn.esprit.studentmanagement.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Métriques métier de l'application. Micrometer publie le compteur sous le nom
 * students_created_total dans /actuator/prometheus.
 */
@Component
public class StudentMetrics {

    private final Counter studentsCreated;

    public StudentMetrics(MeterRegistry registry) {
        this.studentsCreated = Counter.builder("students.created")
                .description("Nombre d'étudiants créés depuis le démarrage")
                .register(registry);
    }

    public void studentCreated() {
        studentsCreated.increment();
    }
}
