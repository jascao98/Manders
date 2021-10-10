package com.teammander.salamander.repository;

import com.teammander.salamander.map.District;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {}
