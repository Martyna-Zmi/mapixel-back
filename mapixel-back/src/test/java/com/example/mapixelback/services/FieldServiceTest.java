package com.example.mapixelback.services;

import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.model.Field;
import com.example.mapixelback.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FieldServiceTest {

    static Field field = new Field();
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private FieldService fieldService;

    @BeforeEach
    void setup(){
        field.setId("123");
        field.setName("water");
        field.setImgSrc("water.png");
        field.setCategory("terrain");
    }
    @Test
    void saveFieldValidDataShouldSaveAndReturnField() {
        when(mongoTemplate.save(field)).thenReturn(field);

        Field result = fieldService.saveField(field);

        assertEquals(field, result);
        verify(mongoTemplate).save(field);
    }

    @Test
    void saveFieldInvalidCategoryShouldThrowException() {
        field.setCategory("Bazooka");
        assertThrows(InvalidDataException.class, () -> fieldService.saveField(field));
        verify(mongoTemplate, never()).save(any());
    }

    @Test
    void saveFieldMissingNameShouldThrowException() {
        field.setName(null);

        assertThrows(InvalidDataException.class, () -> fieldService.saveField(field));
        verify(mongoTemplate, never()).save(any());
    }
    @Test
    void saveFieldMissingImgSrcShouldThrowException() {
        field.setImgSrc(null);

        assertThrows(InvalidDataException.class, () -> fieldService.saveField(field));
        verify(mongoTemplate, never()).save(any());
    }
    @Test
    void findFieldByIdExistingIdShouldReturnField() {
        when(mongoTemplate.findById("123", Field.class)).thenReturn(field);

        Field result = fieldService.findFieldById("123");

        assertEquals(field, result);
    }

    @Test
    void findFieldByIdNonexistentIdShouldThrowException() {
        when(mongoTemplate.findById("021", Field.class)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> fieldService.findFieldById("021"));
    }

    @Test
    void findAllFieldsShouldReturnListOfFields() {
        List<Field> fields = List.of(field);
        when(mongoTemplate.findAll(Field.class)).thenReturn(fields);

        List<Field> result = fieldService.findAllFields();

        assertEquals(fields, result);
    }
}