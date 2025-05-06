package com.example.mapixelback.dto;

import com.example.mapixelback.model.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class DtoMapperTest {
    private final DtoMapper dtoMapper = new DtoMapper();
    private final MapSummaryDto exampleMapSummaryDto = new MapSummaryDto("123", "world", 10, 10);
    private final MapFullDto exampleMapFullDto = new MapFullDto();

    private final Map map = new Map("123", "world", "456", 10, 10, new ArrayList<>());
    @BeforeEach
    void setup(){

        exampleMapFullDto.setId("123");
        exampleMapFullDto.setName("world");
        exampleMapFullDto.setDimensionX(10);
        exampleMapFullDto.setDimensionY(10);
        exampleMapFullDto.setFields(new ArrayList<>());
        exampleMapFullDto.setUserId("456");
    }

    @Test
    void shouldMapToSummary(){
        var summary = dtoMapper.mapToSummaryDto(map);
        assertEquals(exampleMapSummaryDto.getId(), summary.getId());
        assertEquals(exampleMapSummaryDto.getName(), summary.getName());
        assertEquals(exampleMapSummaryDto.getDimensionX(), summary.getDimensionX());
        assertEquals(exampleMapSummaryDto.getDimensionY(), summary.getDimensionY());
    }

    @Test
    void shouldMapToFullDto(){
        var full = dtoMapper.mapToMapFullDto(map, new ArrayList<>());
        assertEquals(exampleMapFullDto.getId(), full.getId());
        assertEquals(exampleMapFullDto.getName(), full.getName());
        assertEquals(exampleMapFullDto.getDimensionX(), full.getDimensionX());
        assertEquals(exampleMapFullDto.getDimensionY(), full.getDimensionY());
        assertEquals(exampleMapFullDto.getUserId(), full.getUserId());
        assertEquals(exampleMapFullDto.getFields(), full.getFields());
    }
}