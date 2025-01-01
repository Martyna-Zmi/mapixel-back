package com.example.mapixelback.controllers;

import com.example.mapixelback.dto.DtoMapper;
import com.example.mapixelback.dto.MapFullDto;
import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.model.Field;
import com.example.mapixelback.model.Map;
import com.example.mapixelback.model.User;
import com.example.mapixelback.services.FieldService;
import com.example.mapixelback.services.MapService;
import com.example.mapixelback.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final DtoMapper dtoMapper = new DtoMapper();
    private static final Logger logger = LoggerFactory.getLogger(MapController.class);
    @PostMapping
    public ResponseEntity<Map> createMap(@RequestBody Map map, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        logger.info("incoming POST request at /maps");
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
        logger.info("incoming GET request at /maps/{id}");
        Map mapFound = mapService.findMapById(id);
        return new ResponseEntity<>(mapFound, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<Map>> getAllMaps() {
        logger.info("incoming GET request at /maps");
        return new ResponseEntity<>(mapService.findAllMaps(), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMapById(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        logger.info("incoming DELETE request at /maps/{id}");
        Map foundMap = mapService.findMapById(id);
        User owner = userService.findUserById(foundMap.getUserId());
        if(userService.verifyUserAccess(token, owner)){
            boolean isDeleted = mapService.deleteMapById(id);
            if(isDeleted){
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    @GetMapping("/{id}/with-fields")
    public ResponseEntity<MapFullDto> getMapFullInfo(@PathVariable String id){
        logger.info("incoming GET request at /maps/{id}/with-fields");
        Map foundMap = mapService.findMapById(id);
        List<Field> mappedFields = foundMap.getFields().stream().map(elementId -> fieldService.findFieldById(elementId)).toList();
        return new ResponseEntity<>(dtoMapper.mapToMapFullDto(foundMap, mappedFields), HttpStatus.OK);
    }
    @PutMapping
    public ResponseEntity<Map> updateMap(@RequestBody Map map, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        logger.info("incoming PUT request at /maps");
            User owner = userService.findUserById(map.getUserId());
            if(userService.verifyUserAccess(token, owner)){
                Map mapFound = mapService.saveMap(map);
                if(mapFound != null){
                    return new ResponseEntity<>(mapFound, HttpStatus.CREATED);
                }
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    }
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Map>> getMapsByUserId(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        if (userService.verifyUserAccess(token, userService.findUserById(id)) || userService.verifyAdminAccess(token)) {
            List<Map> mapsFound = mapService.findMapsByUserId(id);
            return new ResponseEntity<>(mapsFound, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
