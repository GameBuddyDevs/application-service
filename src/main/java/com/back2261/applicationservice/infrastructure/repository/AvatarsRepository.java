package com.back2261.applicationservice.infrastructure.repository;

import com.back2261.applicationservice.infrastructure.entity.Avatars;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarsRepository extends JpaRepository<Avatars, UUID> {

    List<Avatars> findAllByIsSpecialFalse();

    List<Avatars> findAllByIsSpecialTrue();
}
