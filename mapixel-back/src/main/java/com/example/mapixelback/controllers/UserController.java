package com.example.mapixelback.controllers;

import com.example.mapixelback.dto.MapSummaryDto;
import com.example.mapixelback.dto.UserSummaryWithMapsDto;
import com.example.mapixelback.model.User;
import com.example.mapixelback.services.MapService;
import com.example.mapixelback.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MapService mapService;
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User userCreated = userService.saveUser(user);
        if(userCreated!=null){
            return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}/with-maps")
    public ResponseEntity<UserSummaryWithMapsDto> getUserWithMaps(@PathVariable String id){
        User foundUser = userService.findUserById(id);
        if(foundUser != null){
            UserSummaryWithMapsDto userDto = new UserSummaryWithMapsDto();
            userDto.setId(foundUser.getId());
            userDto.setUsername(foundUser.getUsername());
            userDto.setEmail(foundUser.getEmail());
            List<String> foundMapsIds = foundUser.getMaps();
            List<MapSummaryDto> foundMapsSummaries = foundMapsIds.stream().map(entry -> mapService.mapToSummaryDto(entry)).toList();
            userDto.setMaps(foundMapsSummaries);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User userFound = userService.findUserById(id);
        if(userFound!=null){
            return new ResponseEntity<>(userFound, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<List<User>> getUsersByUsername(@PathVariable String username) {
        return new ResponseEntity<>(userService.findUsersByUsername(username), HttpStatus.OK);
    }
}