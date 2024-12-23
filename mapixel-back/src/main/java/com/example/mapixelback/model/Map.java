package com.example.mapixelback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "maps")
public class Map {
    @Id
    private String id;
    private String name;
    private String userId; //reference to user
    private int dimensionX;
    private int dimensionY;
    private List<String> fields; //list of references to objects

}
