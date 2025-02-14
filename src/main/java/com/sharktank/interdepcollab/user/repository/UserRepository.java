package com.sharktank.interdepcollab.user.repository;
import com.sharktank.interdepcollab.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);
}