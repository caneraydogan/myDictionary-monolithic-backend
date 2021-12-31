package com.caner.service;

import com.caner.bean.*;

import java.util.List;

public interface EntryService {
    ResultBean<Entry> findEntry(String userUUId, String word, Boolean donePracticing);

    Result deleteEntry(Long entryId);

    Result saveEntry(EntryDTO entryDTO);

    Result updateEntry(EntryDTO entryDTO);

    ResultBean<List<Entry>> findAll(String userUUId, Boolean donePracticing);

    ResultBean<Entry> findRandomEntry(String userUUId, Boolean donePracticing);

    Result updatePractice(EntryDTO entryDTO, Boolean practiceValue);

    ResultBean<List<Entry>> findEntryByWord(String userUUId, String word);

}
