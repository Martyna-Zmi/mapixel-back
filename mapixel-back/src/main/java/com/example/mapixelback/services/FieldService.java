package com.example.mapixelback.services;

import com.example.mapixelback.model.Field;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FieldService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Field saveField(Field field) {
        return mongoTemplate.save(field);
    }
    public Field findFieldById(String id) {
        return mongoTemplate.findById(id, Field.class);
    }
    public List<Field> findAllFields() {
        return mongoTemplate.findAll(Field.class);
    }


}
