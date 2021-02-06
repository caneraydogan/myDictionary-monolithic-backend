package com.caner.dao;

import com.caner.bean.GermanMeaning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GermanMeaningRepo extends JpaRepository<GermanMeaning, Long> {

    GermanMeaning findMeaningById(Long id);
}
