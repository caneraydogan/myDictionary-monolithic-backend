package com.caner.dao;

import com.caner.bean.GermanEntry;
import com.caner.bean.GermanUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GermanUsageRepo extends JpaRepository<GermanUsage, Long> {

    List<GermanUsage> findByEntry(GermanEntry entry);

}
