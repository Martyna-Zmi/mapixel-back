package com.example.mapixelback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@Getter
@Setter
public class UserSummaryWithMapsDto {
    private String id;
    private String username;
    private String email;
    private List<MapSummaryDto> maps;
}
