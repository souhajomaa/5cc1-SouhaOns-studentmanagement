package tn.esprit.studentmanagement.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.services.IStudentService;

@WebMvcTest(StudentController.class)
 class StudentControllerTest {
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
    @Test
void getStudentById_retourne200() throws Exception {
    when(studentService.getStudentById(1L)).thenReturn(new Student());
    mockMvc.perform(get("/students/getStudent/1"))
           .andExpect(status().isOk());
}

@Test
void getStudentByEmail_retourne200() throws Exception {
    when(studentService.getStudentByEmail("a@test.tn")).thenReturn(new Student());
    mockMvc.perform(get("/students/getStudentByEmail/a@test.tn"))
           .andExpect(status().isOk());
}

@Test
void deleteStudent_retourne200() throws Exception {
    mockMvc.perform(delete("/students/deleteStudent/1"))
           .andExpect(status().isOk());
}
}
