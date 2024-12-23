package com.example.mapixelback.dto;

import com.example.mapixelback.model.Field;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@Getter
@Setter
public class MapFullDto {
    private String id;
    private String name;
    private String userId;
    private int dimensionX;
    private int dimensionY;
    private List<Field> fields;
}
