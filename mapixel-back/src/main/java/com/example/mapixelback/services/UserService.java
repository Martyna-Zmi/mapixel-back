package com.example.mapixelback.services;

import com.example.mapixelback.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    public User createUser(User user){
        String password = user.getPassword();
        User userFromDb = findUserByEmail(user.getEmail());
        if(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,40}$") && userFromDb==null){
            String encodedPassword = encoder.encode(password);
            user.setPassword(encodedPassword);
            return mongoTemplate.save(user);
        }
        return null;
    }

    public User findUserById(String id) {
        return mongoTemplate.findById(id, User.class);
    }
    public User authorizeUser(String email, String password){
        User userFound = findUserByEmail(email);
        if(userFound != null && encoder.matches(password, userFound.getPassword())){
            return userFound;
        }
        return null;
    }
    public User findUserByEmail(String email){
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, User.class);
    }
    public List<User> findAllUsers() {
        return mongoTemplate.findAll(User.class);
    }
    public List<User> findUsersByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.find(query, User.class);
    }
}
