package com.example.mapixelback.controllers;

import com.example.mapixelback.dto.*;
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
    private final DtoMapper dtoMapper = new DtoMapper();
    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @PostMapping("/authorize")
    public ResponseEntity<TokenResponse> login(@RequestBody UserAuth userAuth) {
        logger.info("incoming POST request at /users/authorize");
        User user = userService.authorizeUser(userAuth.getEmail(), userAuth.getPassword());

        final UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
        TokenResponse tokenResponse = new TokenResponse(jwtUtil.generateToken(userDetails));

        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
    @PostMapping("/create")
    public ResponseEntity<TokenResponse> createUser(@RequestBody User user){
        logger.info("incoming POST request at /users/create");
        User userCreated = userService.createUser(user);

        final UserDetails userDetails = new org.springframework.security.core.userdetails.User(userCreated.getEmail(), userCreated.getPassword(), new ArrayList<>());
        TokenResponse tokenResponse = new TokenResponse(jwtUtil.generateToken(userDetails));

        return new ResponseEntity<>(tokenResponse, HttpStatus.CREATED);
    }
    @GetMapping("/extract")
    public ResponseEntity<Map<String, String>> userIdFromToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        User user = userService.findUserByEmail(jwtUtil.extractUsernameFromToken(token.replace("Bearer ", "")));
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
                List<MapSummaryDto> foundMapsSummaries = foundMapsIds.stream().map(mapId -> dtoMapper.mapToSummaryDto(mapService.findMapById(mapId))).toList();

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
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Incoming GET request at /users with page={} and size={}", page, size);
        Map<String, Object> response = userService.findAllUsers(token, page, size);
        if (response == null) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<List<User>> getUsersByUsername(@PathVariable String username) {
        logger.info("incoming GET request at /users/username/{username}");
        return new ResponseEntity<>(userService.findUsersByUsername(username), HttpStatus.OK);
    }
}