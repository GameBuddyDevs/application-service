package com.back2261.applicationservice.infrastructure.repository;

import com.back2261.applicationservice.infrastructure.entity.Keywords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordsRepository extends JpaRepository<Keywords, String> {}
