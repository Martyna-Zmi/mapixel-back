package com.example.mapixelback.controllers;

import com.example.mapixelback.dto.MapFullDto;
import com.example.mapixelback.model.Field;
import com.example.mapixelback.model.Map;
import com.example.mapixelback.services.FieldService;
import com.example.mapixelback.services.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maps")
public class MapController {

    @Autowired
    private MapService mapService;
    @Autowired
    private FieldService fieldService;
    @PostMapping
    public ResponseEntity<Map> createMap(@RequestBody Map map) {
        Map mapCreated = mapService.saveMap(map);
        if(mapCreated != null){
            return new ResponseEntity<>(mapCreated, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map> getMapById(@PathVariable String id) {
        Map mapFound = mapService.findMapById(id);
        if(mapFound != null){
            return new ResponseEntity<>(mapFound, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @GetMapping
    public ResponseEntity<List<Map>> getAllMaps() {
        return new ResponseEntity<>(mapService.findAllMaps(), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMapById(@PathVariable String id) {
        boolean isDeleted = mapService.deleteMapById(id);
        if(isDeleted){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}/with-fields")
    public ResponseEntity<MapFullDto> getMapFullInfo(@PathVariable String id){
        Map foundMap = mapService.findMapById(id);
        if(foundMap != null){
            List<Field> mappedFields = foundMap.getFields().stream().map(elementId -> fieldService.findFieldById(elementId)).toList();
            MapFullDto mapDto = new MapFullDto();
            mapDto.setId(foundMap.getId());
            mapDto.setName(foundMap.getName());
            mapDto.setDimensionX(foundMap.getDimensionX());
            mapDto.setDimensionY(foundMap.getDimensionY());
            mapDto.setFields(mappedFields);
            return new ResponseEntity<>(mapDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
