package com.example.mapixelback.controllers;

import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.model.Field;
import com.example.mapixelback.services.FieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fields")
public class FieldController {

    @Autowired
    private FieldService fieldService;
    private static final Logger logger = LoggerFactory.getLogger(FieldController.class);

    @PostMapping
    //TODO: veryfy that the user trying to create a field is an admin
    public ResponseEntity<Field> createField(@RequestBody Field field) {
        logger.info("incoming POST request at /fields");
        return new ResponseEntity<>(fieldService.saveField(field), HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Field> getFieldById(@PathVariable String id) {
        logger.info("incoming GET request at /fields/{id}");
        Field fieldFound = fieldService.findFieldById(id);
        if(fieldFound != null){
            return new ResponseEntity<>(fieldFound, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Field with the following id doesn't exist");
    }
    @GetMapping
    public ResponseEntity<List<Field>> getAllFields() {
        logger.info("incoming GET request at /fields");
        return new ResponseEntity<>(fieldService.findAllFields(), HttpStatus.OK);
    }
}
