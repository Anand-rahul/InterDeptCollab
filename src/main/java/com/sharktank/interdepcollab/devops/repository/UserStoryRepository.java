package com.sharktank.interdepcollab.devops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharktank.interdepcollab.devops.model.UserStory;

public interface UserStoryRepository extends JpaRepository<UserStory, Integer> {
}