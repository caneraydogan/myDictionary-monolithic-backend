package com.caner.dao;

import com.caner.bean.EnglishEntry;
import com.caner.bean.EnglishUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnglishUsageRepo extends JpaRepository<EnglishUsage, Long> {

    EnglishUsage findUsageById(Long id);

    List<EnglishUsage> findByEntry(EnglishEntry entry);

}
