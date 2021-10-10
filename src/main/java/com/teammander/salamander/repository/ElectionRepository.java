package com.teammander.salamander.repository;

import com.teammander.salamander.data.Election;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionRepository extends JpaRepository<Election, Integer> {}