package com.sharktank.interdepcollab.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharktank.interdepcollab.user.model.User;
import com.sharktank.interdepcollab.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Dictionary;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CommonsLog
public class UserController {
    @Autowired
    private final UserService _userService;

    @GetMapping
    public User getUser(@RequestBody User user) {
        if (user.getEmployeeId() != null) {
            return _userService.getUserById(user.getEmployeeId()).orElseThrow();
        }
        if (!user.getEmail().isEmpty()) {
            return _userService.getUserByEmail(user.getEmail()).orElseThrow();
        } else {
            log.error("No user found");
            throw new NoSuchElementException("No user found");
        }
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return _userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return _userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        Integer id = user.getEmployeeId();
        return _userService.updateUser(id, user);
    }

    @DeleteMapping
    public void deleteUser(@RequestBody User user) {
        Integer id = user.getEmployeeId();
        _userService.deleteUser(id);
    }
}
