package com.example.mapixelback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class MapSummaryDto {
    private String id;
    private String name;
    private int dimensionX;
    private int dimensionY;
}
