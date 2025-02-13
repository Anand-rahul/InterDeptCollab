package com.sharktank.interdepcollab.ai.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharktank.interdepcollab.ai.Model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatGuidOrderByTimestamp(UUID chatGuid);
}
