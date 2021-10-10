package com.teammander.salamander.repository;

import com.teammander.salamander.data.DemographicData;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoDataRepository extends JpaRepository<DemographicData, Integer> {}