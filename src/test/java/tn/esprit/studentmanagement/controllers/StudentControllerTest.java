package tn.esprit.studentmanagement.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tn.esprit.studentmanagement.services.IStudentService;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    IStudentService studentService;

    @Test
    void getAllStudents_retourne200() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());
        mockMvc.perform(get("/students/getAllStudents"))
                .andExpect(status().isOk());
    }
}
