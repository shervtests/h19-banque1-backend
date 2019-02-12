package com.grokonez.spring.restapi.mongodb.service;

import com.grokonez.spring.restapi.mongodb.bean.UserBean;
import com.grokonez.spring.restapi.mongodb.config.DataSource;
import com.grokonez.spring.restapi.mongodb.model.User;
import com.grokonez.spring.restapi.mongodb.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUser(String username) {
        // return null if no such user
//        if (!DataSource.getData().containsKey(username))
//            return null;
//
//        UserBean user = new UserBean();
//        Map<String, String> detail = DataSource.getData().get(username);
//
//        user.setUsername(username);
//        user.setPassword(detail.get("password"));
//        user.setRole(detail.get("role"));
//        user.setPermission(detail.get("permission"));
//        return user;

        return userRepository.findByUsername(username);

    }
}