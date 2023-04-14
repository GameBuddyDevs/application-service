package com.back2261.applicationservice.infrastructure.repository;

import com.back2261.applicationservice.infrastructure.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {}
