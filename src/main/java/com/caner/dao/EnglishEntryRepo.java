package com.caner.dao;

import com.caner.bean.EnglishEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnglishEntryRepo extends JpaRepository<EnglishEntry, Long> {

    EnglishEntry findEntryById(Long id);


    @Query(value = "select e" +
            "         from EnglishEntry e " +
            "        where upper(e.word) = upper(:word)")
    EnglishEntry findEntryByWord(String word);

    @Query(value = "select e" +
            "         from EnglishEntry e " +
            "        where e.user.userUUId = :userUUId" +
            "          and upper(e.word) like %:word%")
    List<EnglishEntry> findEntryByUserUUIdAndWordLike(String userUUId, String word);

    @Query(value = "select distinct e" +
            "         from EnglishEntry e " +
            "         join fetch e.meaningList ml" +
            "        where e.user.userUUId = :userUUId" +
            "          and e.donePracticing = :donePracticing")
    List<EnglishEntry> findAllByUserUUIdWithMeaningList(String userUUId, boolean donePracticing);

    @Query(value = "select e" +
            "         from EnglishEntry e" +
            "        where e.randomOrder = :randomOrder" +
            "          and e.user.userUUId = :userUUId")
    List<EnglishEntry> findByRandomOrder(String userUUId, long randomOrder);

    @Query(value = "select e" +
            "         from EnglishEntry e " +
            "        where e.randomOrder between :min and :max" +
            "          and e.user.userUUId = :userUUId" +
            "          and e.donePracticing = :donePracticing")
    List<EnglishEntry> findByRandomOrderLimit(String userUUId, long min, long max, boolean donePracticing);

    @Query(value = "select min(e.randomOrder)" +
            "         from EnglishEntry e" +
            "        where e.user.userUUId = :userUUId" +
            "          and e.donePracticing = :donePracticing")
    long findMinimumRandomOrderEntry(String userUUId, boolean donePracticing);

    @Query(value = "SELECT nextval('random_order_seq_english')"
            , nativeQuery = true)
    Long findRandomOrderSeqValue();

    @Modifying
    @Query(" update EnglishEntry e " +
            "   set e.donePracticing = :donePracticing " +
            " where e.id =:entryId")
    void updateDonePracticing(long entryId, boolean donePracticing);

}
