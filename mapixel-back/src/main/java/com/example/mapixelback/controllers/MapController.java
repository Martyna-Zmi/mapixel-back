package com.example.mapixelback.controllers;

import com.example.mapixelback.dto.MapFullDto;
import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.model.Field;
import com.example.mapixelback.model.Map;
import com.example.mapixelback.model.User;
import com.example.mapixelback.services.FieldService;
import com.example.mapixelback.services.MapService;
import com.example.mapixelback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    @Autowired
    private UserService userService;
    @PostMapping
    public ResponseEntity<Map> createMap(@RequestBody Map map, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        User owner = userService.findUserById(map.getUserId());
        if(owner!=null && userService.verifyUserAccess(token, owner)){
            Map mapCreated = mapService.saveMap(map);
            if(mapCreated != null){
                return new ResponseEntity<>(mapCreated, HttpStatus.CREATED);
            }
            throw new InvalidDataException("Invalid data for creating a map");
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map> getMapById(@PathVariable String id) {
        Map mapFound = mapService.findMapById(id);
        if(mapFound != null){
            return new ResponseEntity<>(mapFound, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Map with this id doesn't exist");
    }
    @GetMapping
    public ResponseEntity<List<Map>> getAllMaps() {
        return new ResponseEntity<>(mapService.findAllMaps(), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMapById(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Map foundMap = mapService.findMapById(id);
        if(foundMap!=null && userService.findUserById(foundMap.getUserId())!=null) {
            User owner = userService.findUserById(foundMap.getUserId());
            if(owner!=null && userService.verifyUserAccess(token, owner)){
                boolean isDeleted = mapService.deleteMapById(id);
                if(isDeleted){
                    return new ResponseEntity<>(HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        throw new ResourceNotFoundException("Cannot delete - the following map doesn't exist");
    }
    @GetMapping("/{id}/with-fields")
    public ResponseEntity<MapFullDto> getMapFullInfo(@PathVariable String id){
        Map foundMap = mapService.findMapById(id);
        if(foundMap != null){
            List<Field> mappedFields = foundMap.getFields().stream().map(elementId -> fieldService.findFieldById(elementId)).toList();
            MapFullDto mapDto = new MapFullDto();
            mapDto.setId(foundMap.getId());
            mapDto.setName(foundMap.getName());
            mapDto.setUserId(foundMap.getUserId());
            mapDto.setDimensionX(foundMap.getDimensionX());
            mapDto.setDimensionY(foundMap.getDimensionY());
            mapDto.setFields(mappedFields);
            return new ResponseEntity<>(mapDto, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Map with this id doesn't exist");
    }
    @PutMapping
    public ResponseEntity<Map> updateMap(@RequestBody Map map, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        if(map.getUserId()!=null){
            User owner = userService.findUserById(map.getUserId());
            if(owner!=null && userService.verifyUserAccess(token, owner)){
                Map mapFound = mapService.saveMap(map);
                if(mapFound != null){
                    return new ResponseEntity<>(mapFound, HttpStatus.CREATED);
                }
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        throw new InvalidDataException("Invalid data for creating a map");
    }
}
