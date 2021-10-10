package com.teammander.salamander.repository;

import com.teammander.salamander.map.Precinct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrecinctRepository extends JpaRepository<Precinct, String> {}
