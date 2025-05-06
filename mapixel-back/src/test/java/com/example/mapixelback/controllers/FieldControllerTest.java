package com.example.mapixelback.controllers;

import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.model.Field;
import com.example.mapixelback.services.FieldService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldControllerTest {
    Field exampleField = new Field();

    @Mock
    FieldService fieldService;
    @Mock
    Logger logger;
    @InjectMocks
    FieldController fieldController;

    @BeforeEach
    void setup(){
        exampleField.setId("123");
        exampleField.setName("water");
        exampleField.setImgSrc("water.png");
        exampleField.setCategory("terrain");
    }
    @Test
    void shouldGetFieldById(){
        when(fieldService.findFieldById("1234")).thenReturn(exampleField);

        assertEquals(exampleField, fieldController.getFieldById("1234").getBody());
        assertTrue(fieldController.getFieldById("1234").getStatusCode().is2xxSuccessful());
    }
    @Test
    void shouldNotGetFieldById(){
        when(fieldService.findFieldById("0987")).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            fieldController.getFieldById("0987");
        });
    }
    @Test
    void shouldGetAllFields(){
        List<Field> fields = new ArrayList<>();
        fields.add(exampleField);
        when(fieldService.findAllFields()).thenReturn(fields);

        assertEquals(fields, fieldController.getAllFields().getBody());
        assertTrue(fieldController.getAllFields().getStatusCode().is2xxSuccessful());
    }
}