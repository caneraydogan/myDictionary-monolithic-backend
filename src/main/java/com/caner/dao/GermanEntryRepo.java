package com.caner.dao;

import com.caner.bean.GermanEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GermanEntryRepo extends JpaRepository<GermanEntry, Long> {

    GermanEntry findEntryById(Long id);


    @Query(value = "select e" +
            "         from GermanEntry e " +
            "        where upper(e.word) = upper(:word)")
    GermanEntry findEntryByWord(String word);

    @Query(value = "select e" +
            "         from GermanEntry e " +
            "        where e.user.userUUId = :userUUId" +
            "          and upper(e.word) like %:word%")
    List<GermanEntry> findEntryByUserUUIdAndWordLike(String userUUId, String word);

    @Query(value = "select distinct e" +
            "         from GermanEntry e " +
            "         join fetch e.meaningList ml" +
            "        where e.user.userUUId = :userUUId" +
            "          and e.donePracticing = :donePracticing")
    List<GermanEntry> findAllByUserUUIdWithMeaningList(String userUUId, boolean donePracticing);

    @Query(value = "select e" +
            "         from GermanEntry e" +
            "        where e.randomOrder = :randomOrder" +
            "          and e.user.userUUId = :userUUId")
    List<GermanEntry> findByRandomOrder(String userUUId, long randomOrder);

    @Query(value = "select e" +
            "         from GermanEntry e " +
            "        where e.randomOrder between :min and :max" +
            "          and e.user.userUUId = :userUUId" +
            "          and e.donePracticing = :donePracticing")
    List<GermanEntry> findByRandomOrderLimit(String userUUId, long min, long max, boolean donePracticing);

    @Query(value = "select min(e.randomOrder)" +
            "         from GermanEntry e" +
            "        where e.user.userUUId = :userUUId" +
            "          and e.donePracticing = :donePracticing")
    long findMinimumRandomOrderEntry(String userUUId, boolean donePracticing);

    @Query(value = "SELECT nextval('random_order_seq_german')"
            , nativeQuery = true)
    Long findRandomOrderSeqValue();

    @Modifying
    @Query(" update GermanEntry e " +
            "   set e.donePracticing = :donePracticing " +
            " where e.id =:entryId")
    void updateDonePracticing(long entryId, boolean donePracticing);
}
