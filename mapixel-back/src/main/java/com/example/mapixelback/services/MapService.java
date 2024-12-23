package com.example.mapixelback.services;

import com.example.mapixelback.dto.MapSummaryDto;
import com.example.mapixelback.model.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Map saveMap(Map map) {
        boolean isUpdating = findMapById(map.getId()) != null;
        if(!isUpdating && findMapsByUserId(map.getUserId()).size()==5){ //check if user already has enough maps
            return null;
        }
        if(map.getName().length()>6){
            mongoTemplate.save(map);
        }
        return null;
    }
    public Map findMapById(String id) {
        return mongoTemplate.findById(id, Map.class);
    }
    public List<Map> findAllMaps() {
        return mongoTemplate.findAll(Map.class);
    }
    public boolean deleteMapById(String id) {
        Map mapFound = findMapById(id);
        if(mapFound != null){
            Query query = new Query(Criteria.where("id").is(id));
            mongoTemplate.remove(query, Map.class);
            return true;
        }
        else return false;
    }
    public List<Map> findMapsByUserId(String userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Map.class);
    }
    public MapSummaryDto mapToSummaryDto(String id){
        Map mapFound = findMapById(id);
        if(mapFound != null){
            return new MapSummaryDto(mapFound.getId(), mapFound.getName(), mapFound.getDimensionX(), mapFound.getDimensionY());
        }
        return null;
    }
}
