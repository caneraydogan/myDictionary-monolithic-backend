package com.caner.dao;

import com.caner.bean.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeaningRepo extends JpaRepository<Meaning, Long> {

    Meaning findMeaningById(Long id);
}
