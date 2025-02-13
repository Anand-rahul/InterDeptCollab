package com.sharktank.interdepcollab.ai.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharktank.interdepcollab.ai.Model.ChatSession;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    Optional<ChatSession> findByGuid(UUID guid);

}
