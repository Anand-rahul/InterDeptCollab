// package com.sharktank.interdepcollab.user.controller;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.sharktank.interdepcollab.user.model.AppUser;
// import com.sharktank.interdepcollab.user.service.UserService;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.RequestBody;

// import java.util.List;
// import java.util.NoSuchElementException;

// @RestController
// @RequestMapping("/login")
// @RequiredArgsConstructor
// @Slf4j
// public class UserController {
    
//     private final UserService _userService;

//     @GetMapping
//     public AppUser getUser(@RequestBody AppUser user) {
//         if (user.getEmployeeId() != null) {
//             return _userService.getUserById(user.getEmployeeId()).orElseThrow();
//         }
//         if (!user.getEmail().isEmpty()) {
//             return _userService.getUserByEmail(user.getEmail()).orElseThrow();
//         } else {
//             log.error("No user found");
//             throw new NoSuchElementException("No user found");
//         }
//     }

//     @GetMapping("/all")
//     public List<AppUser> getAllUsers() {
//         return _userService.getAllUsers();
//     }

//     @PostMapping
//     public AppUser createUser(@RequestBody AppUser user) {
//         return _userService.addUser(user);
//     }

//     @PutMapping
//     public AppUser updateUser(@RequestBody AppUser user) {
//         Integer id = user.getEmployeeId();
//         return _userService.updateUser(id, user);
//     }

//     @DeleteMapping
//     public void deleteUser(@RequestBody AppUser user) {
//         Integer id = user.getEmployeeId();
//         _userService.deleteUser(id);
//     }
// }
