package com.caner.service;

import com.caner.bean.EnglishEntry;
import com.caner.bean.EnglishEntryDTO;
import com.caner.bean.Result;
import com.caner.bean.ResultBean;

import java.util.List;

public interface EnglishEntryService {
    ResultBean<EnglishEntry> findEntry(Long entryId);

    Result deleteEntry(Long entryId);

    Result saveEntry(EnglishEntryDTO entryDTO);

    Result updateEntry(EnglishEntryDTO entryDTO);

    ResultBean<List<EnglishEntry>> findAll(String userUUId, Boolean donePracticing);

    ResultBean<EnglishEntry> findRandomEntry(String userUUId, Boolean donePracticing);

    Result updatePractice(Long entryId, Boolean practiceValue);

    ResultBean<List<EnglishEntry>> findEntryByWord(String userUUId, String word);
}
