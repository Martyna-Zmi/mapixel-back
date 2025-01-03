package com.example.mapixelback.services;

import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.model.Map;
import com.example.mapixelback.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MapService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private FieldService fieldService;

    public Map saveMap(Map map) {
        boolean isUpdating = map.getId() != null;
        if(isUpdating && findMapById(map.getId())==null){
            throw new InvalidDataException("Map with this id doesn't exist");
        }
        if(map.getName()== null || map.getName().length()<4 || map.getName().length()>20){
            throw new InvalidDataException("Invalid map name");
        }
        if(map.getUserId()==null) throw new InvalidDataException("Please provide user id");
        User userFromDb = userService.findUserById(map.getUserId());
        if(map.getDimensionY()>20 || map.getDimensionY()<10 || map.getDimensionX()>20 || map.getDimensionX()<10){
            throw new InvalidDataException("Map dimensions can only be from 10 to 20 tiles each");
        }
        if(!isUpdating){ //fill the map with one type of field if the map is created
            if(map.getFields()==null || map.getFields().size()>1 || fieldService.findFieldById(map.getFields().get(0)) == null){
                throw new InvalidDataException("Fields should contain only one item - an id of a field to fill the map with");
            }
            String idOfFieldToFill = map.getFields().get(0);
            List<String> filledFields = new ArrayList<>();
            for(int i = 0; i<map.getDimensionX()* map.getDimensionY(); i++){
                filledFields.add(i, idOfFieldToFill);
            }
            map.setFields(filledFields);
        }
        else if(map.getFields().size() != map.getDimensionX() * map.getDimensionY()){
            System.out.println("Incorrect number of fields");
            throw new InvalidDataException("Incorrect number of fields");
        }
        Map savedMap = mongoTemplate.save(map);
        if(!isUpdating){
            userService.updateUserMaps(userFromDb, savedMap); //add relation to user
        }
        return savedMap;
    }
    public Map findMapById(String id) {
        Map mapFound = mongoTemplate.findById(id, Map.class);
        if(mapFound == null) throw new ResourceNotFoundException("Map with this id doesn't exist");
        return mapFound;
    }
    public List<Map> findAllMaps() {
        return mongoTemplate.findAll(Map.class);
    }
    public boolean deleteMapById(String id) {
        Map mapFound = findMapById(id);
        User owner = userService.findUserById(mapFound.getUserId());
        owner.getMaps().remove(mapFound.getId());

        mongoTemplate.save(owner);
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, Map.class);

        return true;
    }
    public List<Map> findMapsByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Map.class);
    }
}
