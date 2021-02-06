package com.caner.service;

import com.caner.bean.*;

import java.util.List;

public interface GermanEntryService {
    ResultBean<GermanEntry> findEntry(Long entryId);

    Result deleteEntry(Long entryId);

    Result saveEntry(GermanEntryDTO germanEntryDTO);

    Result updateEntry(GermanEntryDTO germanEntryDTO);

    ResultBean<List<GermanEntry>> findAll(String userUUId, Boolean donePracticing);

    ResultBean<GermanEntry> findRandomEntry(String userUUId, Boolean donePracticing);

    Result updatePractice(Long entryId, Boolean practiceValue);

    ResultBean<List<GermanEntry>> findEntryByWord(String userUUId, String word);

}
