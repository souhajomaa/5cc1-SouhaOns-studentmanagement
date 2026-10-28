package tn.esprit.studentmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.studentmanagement.entities.Student;
import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
        Optional<Student> findByEmail(String email);
}
