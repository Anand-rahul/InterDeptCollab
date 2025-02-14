package com.sharktank.interdepcollab.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.sharktank.interdepcollab.exception.UserExistsException;
import com.sharktank.interdepcollab.user.model.AppUser;
import com.sharktank.interdepcollab.user.model.AuthenticatedUser;
import com.sharktank.interdepcollab.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public AuthenticatedUser findMatch(String email) {
    Optional<AppUser> user = userRepository.findByEmail(email);
    if (!user.isPresent()) {
      log.info("User not found: " + email);
      throw new UsernameNotFoundException("UserName " + email + " doesn't exist");
    }

    AppUser userInstance = user.get();
    log.info("User Instance: " + userInstance);

    // For multiple content level access, use content-level permissions for each role
    // Mapping: USER entity - (1-n) Role entity - (1-n) Permission entity
    GrantedAuthority authority = () -> userInstance.getRole().name();

    Set<GrantedAuthority> authorities = new HashSet<>(Arrays.asList(authority));

    return new AuthenticatedUser(userInstance, authorities);
  }

  // Get currently logged in user's entity instance (for business logic)
  public AppUser getLoggedInUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication.getPrincipal() == null)
      return null;

    return ((AuthenticatedUser) authentication.getPrincipal()).getUser();
  }

  // public List<AppUser> getAllUsers() {
  //     List<AppUser> users = new ArrayList<>();
  //     userRepository.findAll().forEach(users::add);
  //     return users;
  // }

  // public Optional<AppUser> getUserById(Integer id) {
  //     return userRepository.findById(id);
  // }

  // TODO: Expose this in controller
  public boolean isUserExists(String email) {
      return userRepository.findByEmail(email).isPresent();
  }

  public Optional<AppUser> getUserByEmail(String email) {
      return userRepository.findByEmail(email);
  }

  public AppUser addUser(AppUser user) {
      Optional<AppUser> existingUser = userRepository.findByEmail(user.getEmail());
      if(existingUser.isPresent()) {
          throw new UserExistsException("User Already Exists");
      }
      return userRepository.save(user);
  }

  public AppUser saveUser(AppUser user) {
      return userRepository.save(user);
  }

  // public AppUser updateUser(Integer id, AppUser user) {
  //     Optional<AppUser> existingUser = userRepository.findById(id);
  //     if (existingUser.isPresent()) {
  //         user.setEmployeeId(id);
  //         return userRepository.save(user);
  //     } else {
  //         return null;
  //     }
  // }

  // public void deleteUser(Integer id) {
  //     userRepository.deleteById(id);
  // }
}
