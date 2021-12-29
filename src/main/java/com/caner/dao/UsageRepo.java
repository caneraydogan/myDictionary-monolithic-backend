package com.caner.dao;

import com.caner.bean.Entry;
import com.caner.bean.Usage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsageRepo extends JpaRepository<Usage, Long> {

    List<Usage> findByEntry(Entry entry);

}
