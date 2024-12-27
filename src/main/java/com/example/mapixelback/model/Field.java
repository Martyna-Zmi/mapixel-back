package com.example.mapixelback.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@Document(collection = "fields")
public class Field {
    @Id
    private String id;
    private String name;
    private String imgSrc;
    private String category;
}
