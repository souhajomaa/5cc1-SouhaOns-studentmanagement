package tn.esprit.studentmanagement.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.metrics.StudentMetrics;
import tn.esprit.studentmanagement.repositories.StudentRepository;

import java.util.List;

@Service
@AllArgsConstructor // injection par constructeur
public class StudentService implements IStudentService {

    private final StudentRepository studentRepository;
    private final StudentMetrics studentMetrics;

    public List<Student> getAllStudents() { return studentRepository.findAll(); }

    public Student getStudentById(Long id) { return studentRepository.findById(id).orElse(null); }

public Student getStudentByEmail(String email) {
    return studentRepository.findByEmail(email).orElse(null);
}
    public Student saveStudent(Student student) {
        Student saved = studentRepository.save(student);
        studentMetrics.studentCreated(); // +1 sur students_created_total
        return saved;
    }

    public void deleteStudent(Long id) { studentRepository.deleteById(id); }
}