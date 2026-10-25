package tn.esprit.studentmanagement;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Nécessite MySQL : test d'intégration, non exécuté en CI")
class StudentManagementApplicationTests {

    @Test
    void contextLoads(
            org.springframework.context.ApplicationContext context) {
        assertNotNull(context, "Spring application context should load");
    }

}