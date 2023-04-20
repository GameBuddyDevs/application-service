package com.back2261.applicationservice.infrastructure.repository;

import com.back2261.applicationservice.infrastructure.entity.Achievements;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementsRepository extends JpaRepository<Achievements, UUID> {}
