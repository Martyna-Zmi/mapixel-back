package com.example.mapixelback.dto;

import com.example.mapixelback.model.Field;
import com.example.mapixelback.model.Map;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@NoArgsConstructor
@Component
public class DtoMapper {
    public MapFullDto mapToMapFullDto(Map map, List<Field> mappedFields){
        MapFullDto mapDto = new MapFullDto();
        mapDto.setId(map.getId());
        mapDto.setName(map.getName());
        mapDto.setUserId(map.getUserId());
        mapDto.setDimensionX(map.getDimensionX());
        mapDto.setDimensionY(map.getDimensionY());
        mapDto.setFields(mappedFields);
        return mapDto;
    }
    public MapSummaryDto mapToSummaryDto(Map map){
        return new MapSummaryDto(map.getId(), map.getName(), map.getDimensionX(), map.getDimensionY());
    }
}
