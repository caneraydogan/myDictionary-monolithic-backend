package com.caner.dao;

import com.caner.bean.EnglishEntry;
import com.caner.bean.EnglishMeaning;
import com.caner.bean.EnglishUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnglishMeaningRepo extends JpaRepository<EnglishMeaning, Long> {

    EnglishMeaning findMeaningById(Long id);

    List<EnglishUsage> findByEntry(EnglishEntry entry);

}
