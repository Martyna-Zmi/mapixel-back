package com.example.mapixelback.controllers;

import com.example.mapixelback.exception.GlobalExceptionHandler;
import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.model.Field;
import com.example.mapixelback.services.FieldService;
import com.example.mapixelback.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FieldControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Field exampleField = new Field();
    private MockMvc mockMvc;
    @Mock
    private FieldService fieldService;
    @Mock
    private UserService userService;

    @InjectMocks
    private FieldController fieldController;

    @BeforeEach
    void setup(){
        exampleField.setId("123");
        exampleField.setName("water");
        exampleField.setImgSrc("water.png");
        exampleField.setCategory("terrain");
        mockMvc = MockMvcBuilders.standaloneSetup(new GlobalExceptionHandler(), fieldController).build();
    }

    @Test
    void shouldGetFieldById() throws Exception {
        //when
        when(fieldService.findFieldById("123")).thenReturn(exampleField);
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/fields/123"))
                .andExpect(jsonPath("$.name").value("water"))
                .andExpect(jsonPath("$.imgSrc").value("water.png"))
                .andExpect(jsonPath("$.category").value("terrain"))
                .andExpect(status().isOk());
        verify(fieldService).findFieldById(any(String.class));
    }

    @Test
    void shouldNotGetFieldByInvalidId() throws Exception {
        //when
        when(fieldService.findFieldById("0987")).thenThrow(ResourceNotFoundException.class);
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/fields/0987")).andExpect(status().isBadRequest());
        verify(fieldService).findFieldById(any(String.class));
    }

    @Test
    void shouldGetAllFields() throws Exception {
        //given
        List<Field> fields = new ArrayList<>();
        fields.add(exampleField);
        //when
        when(fieldService.findAllFields()).thenReturn(fields);
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/fields"))
                .andExpect(jsonPath("$[*].name").value(containsInAnyOrder(exampleField.getName())))
                .andExpect(jsonPath("$[*].imgSrc").value(containsInAnyOrder(exampleField.getImgSrc())))
                .andExpect(jsonPath("$[*].category").value(containsInAnyOrder(exampleField.getCategory())))
                .andExpect(status().isOk());
        verify(fieldService).findAllFields();
    }

    @Test
    void shouldCreateField() throws Exception {
        //when
        when(userService.verifyAdminAccess("Bearer valid-token")).thenReturn(true);
        when(fieldService.saveField(any(Field.class))).thenReturn(exampleField);
        //then
        mockMvc.perform(post("/fields")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exampleField)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("terrain"))
                .andExpect(jsonPath("$.name").value("water"))
                .andExpect(jsonPath("$.imgSrc").value("water.png"));

        verify(userService).verifyAdminAccess("Bearer valid-token");
        verify(fieldService).saveField(any(Field.class));
    }

    @Test
    void shouldNotCreateFieldUnauthorized() throws Exception {
        //when
        when(userService.verifyAdminAccess("Bearer invalid-token")).thenReturn(false);
        //then
        mockMvc.perform(post("/fields")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exampleField)))
                .andExpect(status().isUnauthorized());

        verify(userService).verifyAdminAccess("Bearer invalid-token");
        verify(fieldService, never()).saveField(any(Field.class));
    }

    @Test
    void shouldNotCreateFieldWithIncompleteData() throws Exception{
        //given
        exampleField.setName(null);
        //when
        when(userService.verifyAdminAccess("Bearer valid-token")).thenReturn(true);
        when(fieldService.saveField(argThat(field -> field.getName()==null)))
                .thenThrow(InvalidDataException.class);
        //then
        mockMvc.perform(post("/fields")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exampleField)))
                .andExpect(status().isBadRequest());
    }
}