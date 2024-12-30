package com.example.mapixelback.controllers;

import com.example.mapixelback.dto.MapSummaryDto;
import com.example.mapixelback.dto.TokenResponse;
import com.example.mapixelback.dto.UserAuth;
import com.example.mapixelback.dto.UserSummaryWithMapsDto;
import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.jwt.JwtUtil;
import com.example.mapixelback.model.User;
import com.example.mapixelback.services.MapService;
import com.example.mapixelback.services.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private MapService mapService;
    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @PostMapping("/authorize")
    public ResponseEntity<TokenResponse> login(@RequestBody UserAuth userAuth) {
        logger.info("incoming POST request at /users/authorize");
        User user = userService.authorizeUser(userAuth.getEmail(), userAuth.getPassword());
        if (user!=null) {
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
            TokenResponse tokenResponse = new TokenResponse(jwtUtil.generateToken(userDetails));
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        } else {
            throw new InvalidDataException("Invalid credentials");
        }
    }
    @PostMapping("/create")
    public ResponseEntity<TokenResponse> createUser(@RequestBody User user){
        logger.info("incoming POST request at /users/create");
        User userCreated = userService.createUser(user);
        if(userCreated == null){
            throw new InvalidDataException("Invalid registration data");
        }
        final UserDetails userDetails = new org.springframework.security.core.userdetails.User(userCreated.getEmail(), userCreated.getPassword(), new ArrayList<>());
        TokenResponse tokenResponse = new TokenResponse(jwtUtil.generateToken(userDetails));
        return new ResponseEntity<>(tokenResponse ,HttpStatus.CREATED);
    }
    @GetMapping("/extract")
    public ResponseEntity<Map<String, String>> userIdFromToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        User user = userService.findUserByEmail(jwtUtil.extractUsernameFromToken(token.replace("Bearer ", "")));
        if(user==null) throw new InvalidDataException("Invalid token");
        Map<String, String> response = new HashMap<>();
        response.put("userId", user.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/emailfree")
    public ResponseEntity<String> isEmailInUse(@RequestBody String email){
        logger.info("incoming GET request at /users/emailfree/{email}");
        User user = userService.findUserByEmail(email);
        if(user==null){
            String jsonString = new JSONObject().put("inuse", false).toString();
            return new ResponseEntity<>(jsonString, HttpStatus.OK);
        }
        String jsonString = new JSONObject().put("inuse", true).toString();
        return new ResponseEntity<>(jsonString,HttpStatus.OK);
    }
    @GetMapping("/{id}/with-maps")
    public ResponseEntity<UserSummaryWithMapsDto> getUserWithMaps(@PathVariable String id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        logger.info("incoming GET request at /users/{id}/with-maps");
        User foundUser = userService.findUserById(id);
        if(foundUser != null){
            if(userService.verifyUserAccess(token, foundUser) || userService.verifyAdminAccess(token)){
                UserSummaryWithMapsDto userDto = new UserSummaryWithMapsDto();
                userDto.setId(foundUser.getId());
                userDto.setUsername(foundUser.getUsername());
                userDto.setEmail(foundUser.getEmail());
                List<String> foundMapsIds = foundUser.getMaps();
                List<MapSummaryDto> foundMapsSummaries = foundMapsIds.stream().map(entry -> mapService.mapToSummaryDto(entry)).toList();
                userDto.setMaps(foundMapsSummaries);
                return new ResponseEntity<>(userDto, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        throw new ResourceNotFoundException("User with this id doesn't exist");
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        logger.info("incoming GET request at /users/{id}");
        User userFound = userService.findUserById(id);
        if(userFound!=null){
            return new ResponseEntity<>(userFound, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("User with this id doesn't exist");
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("incoming GET request at /users");
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<List<User>> getUsersByUsername(@PathVariable String username) {
        logger.info("incoming GET request at /users/username/{username}");
        return new ResponseEntity<>(userService.findUsersByUsername(username), HttpStatus.OK);
    }
}