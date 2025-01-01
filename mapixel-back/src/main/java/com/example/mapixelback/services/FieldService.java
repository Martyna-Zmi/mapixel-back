package com.example.mapixelback.services;

import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.model.Field;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FieldService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Field saveField(Field field) {

            if(field.getCategory()!=null && field.getName()!=null && field.getImgSrc()!=null){
                if(Objects.equals(field.getCategory(), "terrain") ||
                        Objects.equals(field.getCategory(), "obstacle") ||
                        Objects.equals(field.getCategory(), "thing") ||
                        Objects.equals(field.getCategory(), "animal")){
                    return mongoTemplate.save(field);
                }
                throw new InvalidDataException("Niepoprawna kategoria pola");
            }
            throw new InvalidDataException("Podaj wszystkie potrzebne dane");
    }
    public Field findFieldById(String id) {
        Field foundField = mongoTemplate.findById(id, Field.class);
        if(foundField == null) throw new ResourceNotFoundException("Field with following id doesn't exist");
        return mongoTemplate.findById(id, Field.class);
    }
    public List<Field> findAllFields() {
        return mongoTemplate.findAll(Field.class);
    }
}
