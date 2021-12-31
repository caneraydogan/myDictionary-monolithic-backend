package com.caner.dao;

import com.caner.bean.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EntryRepo extends JpaRepository<Entry, Long> {

    Entry findEntryById(Long id);


    @Query(value = "select e" +
            "         from Entry e " +
            "        where upper(e.word) = upper(:word)")
    Entry findEntryByWord(String word);


    @Query(value = "select distinct e" +
            "         from Entry e " +
            "         join fetch e.meaningList ml" +
            "        where e.user.userUUId = :userUUId" +
            "          and e.donePracticing = :donePracticing")
    List<Entry> findAllByUserUUIdWithMeaningList(String userUUId, boolean donePracticing);

    @Modifying
    @Query(" update Entry e " +
            "   set e.donePracticing = :donePracticing " +
            " where e.id =:entryId")
    void updateDonePracticing(long entryId, boolean donePracticing);
}
