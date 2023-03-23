package com.back2261.applicationservice.infrastructure.repository;

import com.back2261.applicationservice.infrastructure.entity.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamesRepository extends JpaRepository<Games, String> {}
