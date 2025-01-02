package com.example.mapixelback.services;

import com.example.mapixelback.exception.InvalidDataException;
import com.example.mapixelback.exception.ResourceNotFoundException;
import com.example.mapixelback.jwt.JwtUtil;
import com.example.mapixelback.model.Map;
import com.example.mapixelback.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private JwtUtil jwtUtil;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    public User createUser(User user){
        if(user.getEmail()==null){
            throw new InvalidDataException("Email is required");
        }
        User userFromDb = null;
        try{
            userFromDb = findUserByEmail(user.getEmail());
        }
        catch (ResourceNotFoundException ignored){

        }

        if(userFromDb!=null){
            throw new InvalidDataException("User with that email already exists");
        }
        else if(user.getPassword() == null || !user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,40}$")){
            throw new InvalidDataException("Password doesn't meet the criteria");
        }
        else if(user.getUsername()==null || user.getUsername().length()<3 || user.getUsername().length()>20){
            throw new InvalidDataException("Username doesn't meet the criteria");
        }
        else if(user.getEmail()==null || !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            throw new InvalidDataException("Invalid e-mail address");
        }
        String encodedPassword = encoder.encode(user.getPassword());
        user.setIsAdmin(false);
        user.setMaps(new ArrayList<>());
        user.setPassword(encodedPassword);
        return mongoTemplate.save(user);
    }
    public void updateUserMaps(User user, Map savedMap){
        List<String> updatedMapList = user.getMaps();
        updatedMapList.add(savedMap.getId());
        user.setMaps(updatedMapList);
        mongoTemplate.save(user);
    }
    public User findUserById(String id) {
        User user = mongoTemplate.findById(id, User.class);
        if(user==null) throw new ResourceNotFoundException("user with following id doesn't exist");
        return mongoTemplate.findById(id, User.class);
    }
    public User authorizeUser(String email, String password){
        User userFound = findUserByEmail(email);
        if(encoder.matches(password, userFound.getPassword())){
            return userFound;
        }
        throw new InvalidDataException("Invalid credentials");
    }
    public User findUserByEmail(String email){
        Query query = new Query(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(query, User.class);
        if(user==null) throw new ResourceNotFoundException("user with following email doesn't exist");
        return mongoTemplate.findOne(query, User.class);
    }
    public java.util.Map<String, Object> findAllUsers(String token, int page, int size) {
        if(!verifyAdminAccess(token)){
            return null;
        }
        Query query = new Query();
        long totalUsers = mongoTemplate.count(query, User.class);
        query.skip((long) (page - 1) * size).limit(size);

        List<User> users = mongoTemplate.find(query, User.class);
        java.util.Map<String, Object> response = new HashMap<>();

        response.put("content", users);
        response.put("totalElements", totalUsers);
        response.put("totalPages", (int) Math.ceil((double) totalUsers / size));
        response.put("currentPage", page);

        return response;
}
    public List<User> findUsersByUsername(String username) {
        Query query = new Query(Criteria.where("username").regex(".*" + username + ".*", "i"));
        return mongoTemplate.find(query, User.class);
    }
    public boolean verifyAdminAccess(String token){
        String emailFromToken = jwtUtil.extractUsernameFromToken(token.replace("Bearer ", ""));
        User userFromDb = findUserByEmail(emailFromToken);
        return (userFromDb != null && userFromDb.getIsAdmin());
    }
    public boolean verifyUserAccess(String token, User userToVerify){
        String emailFromToken = jwtUtil.extractUsernameFromToken(token.replace("Bearer ", ""));
        User userFromDb = findUserByEmail(emailFromToken);
        return (userFromDb != null && userFromDb.getEmail().equals(userToVerify.getEmail()));
    }

}
