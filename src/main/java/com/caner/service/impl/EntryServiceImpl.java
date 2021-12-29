package com.caner.service.impl;

import com.caner.bean.*;
import com.caner.dao.EntryRepo;
import com.caner.dao.MeaningRepo;
import com.caner.dao.UsageRepo;
import com.caner.dao.UserRepo;
import com.caner.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class EntryServiceImpl implements EntryService {

    private EntryRepo entryRepo;
    private UsageRepo usageRepo;
    private MeaningRepo meaningRepo;
    private UserRepo userRepo;

    @Autowired
    public EntryServiceImpl(EntryRepo entryRepo, UsageRepo usageRepo, MeaningRepo meaningRepo, UserRepo userRepo) {
        this.entryRepo = entryRepo;
        this.usageRepo = usageRepo;
        this.meaningRepo = meaningRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ResultBean<Entry> findEntry(Long entryId) {

        ResultBean<Entry> result = new ResultBean<>(ResultStatus.OK);

        Entry entry = entryRepo.findEntryById(entryId);

        if (entry == null) {
            result.setStatus(ResultStatus.FAIL).setErrorCode("ENTRY_NOT_FOUND");
        } else {
            List<Usage> usageList = usageRepo.findByEntry(entry);
            entry.setUsageList(usageList);
            result.setData(entry);
        }

        return result;
    }

    @Override
    public Result deleteEntry(Long entryId) {
        entryRepo.deleteById(entryId);
        return new Result().setStatus(ResultStatus.OK);
    }

    public ResultBean<List<Entry>> findAll(String userUUId, Boolean donePracticing) {
        ResultBean<List<Entry>> resultBean = new ResultBean<>();
        List<Entry> entriesWithMeaningList = entryRepo.findAllByUserUUIdWithMeaningList(userUUId, donePracticing);

        for (Entry entry : entriesWithMeaningList) {
            List<Usage> usageList = usageRepo.findByEntry(entry);
            entry.setUsageList(usageList);
        }
        resultBean.setData(entriesWithMeaningList);
        return resultBean;
    }

    @Override
    public Result saveEntry(EntryDTO entryDTO) {
        Result result = validateEntry(entryDTO);

        if (result.isOk()) {
            User user = userRepo.findByUserUUId(entryDTO.getUserUUId());

            if (user == null) {
                return result.setStatus(ResultStatus.FAIL).setMessage("INVALID_USER");
            }

            Entry entry = new Entry();
            entry.setUser(user);
            entry.setWord(entryDTO.getWord());
            entry.setArtikel(entryDTO.getArtikel());
            entry.setDonePracticing(entryDTO.isDonePracticing());
            entry.setRandomOrder(0L);
            entryRepo.save(entry);

            for (Meaning meaning : entryDTO.getMeaningList()) {
                if (!ObjectUtils.isEmpty(meaning.getValue())) {
                    Meaning meaning1 = new Meaning();
                    meaning1.setEntry(entry);
                    meaning1.setValue(meaning.getValue());
                    meaningRepo.save(meaning1);
                }
            }

            for (Usage usage : entryDTO.getUsageList()) {
                if (!ObjectUtils.isEmpty(usage.getValue())) {
                    Usage usage1 = new Usage();
                    usage1.setEntry(entry);
                    usage1.setValue(usage.getValue());
                    usageRepo.save(usage1);
                }
            }

        }
        return result;
    }

    @Override
    public Result updateEntry(EntryDTO entryDTO) {
        Result result = validateEntry(entryDTO);

        if (result.isOk()) {
            Entry entry = entryRepo.findEntryById(entryDTO.getId());
            entry.setWord(entryDTO.getWord());
            entry.setArtikel(entryDTO.getArtikel());
            entry.setDonePracticing(entryDTO.isDonePracticing());
            entryRepo.saveAndFlush(entry);

            List<Meaning> toBeDeletedMeanings = new ArrayList<>();
            for (Meaning meaningDB : entry.getMeaningList()) {
                boolean shouldMeaningBeDeleted = true;
                for (Meaning meaningDTO : entryDTO.getMeaningList()) {
                    if (meaningDB.getId() == meaningDTO.getId()) {
                        meaningDB.setValue(meaningDTO.getValue());
                        shouldMeaningBeDeleted = false;
                        meaningRepo.save(meaningDB);
                        break;
                    }
                }
                if (shouldMeaningBeDeleted) {
                    toBeDeletedMeanings.add(meaningDB);
                }
            }

            for (Meaning meaning : entryDTO.getMeaningList()) {
                if (meaning.getId() == null) {
                    Meaning meaning1 = new Meaning();
                    meaning1.setEntry(entry);
                    meaning1.setValue(meaning.getValue());
                    meaningRepo.save(meaning1);
                }
            }

            List<Usage> toBeDeletedUsages = new ArrayList<>();
            for (Usage usageDB : entry.getUsageList()) {
                boolean shouldUsageBeDeleted = true;
                for (Usage usageDTO : entryDTO.getUsageList()) {
                    if (usageDB.getId() == usageDTO.getId()) {
                        usageDB.setValue(usageDTO.getValue());
                        shouldUsageBeDeleted = false;
                        usageRepo.save(usageDB);
                        break;
                    }
                }
                if (shouldUsageBeDeleted) {
                    toBeDeletedUsages.add(usageDB);
                }
            }

            for (Usage usage : entryDTO.getUsageList()) {
                if (usage.getId() == null) {
                    Usage usage1 = new Usage();
                    usage1.setEntry(entry);
                    usage1.setValue(usage.getValue());
                    usageRepo.save(usage1);
                }
            }

            meaningRepo.deleteInBatch(toBeDeletedMeanings);
            usageRepo.deleteInBatch(toBeDeletedUsages);

        }
        return result;
    }

    public Result validateEntry(EntryDTO entryDTO) {

        Result result = new Result().setStatus(ResultStatus.FAIL);

        if (entryDTO.getUserUUId() == null) {
            result.setErrorCode("MISSING_USER");
        } else if (entryDTO.getWord() == null) {
            result.setErrorCode("MISSING_WORD");
        } else if (entryDTO.getMeaningList().isEmpty()) {
            result.setErrorCode("MISSING_MEANING");
        } else if (entryDTO.getArtikel() != null && !Artikel.isValid(entryDTO.getArtikel().toUpperCase())) {
            result.setErrorCode("WRONG_ARTIKEL");
        } else {
            Entry entry = entryRepo.findEntryByWord(entryDTO.getWord());
            if (entry != null && entry.getId() != entryDTO.getId()) {
                result.setErrorCode("WORD_ALREADY_EXISTS");
            }
        }

        return result.getErrorCode() == null ? result.setStatus(ResultStatus.OK) : result;
    }


    public ResultBean<Entry> findRandomEntry(String userUUId, Boolean donePracticing) {
        ResultBean<Entry> result = new ResultBean<>(ResultStatus.OK);

        long randomOrderSeqValue = entryRepo.findRandomOrderSeqValue();
        long minRandomOrderValue;
        try {
            minRandomOrderValue = entryRepo.findMinimumRandomOrderEntry(userUUId, donePracticing);
        } catch (Exception e) {
            return result.setResultCodeAndMessage(ResultStatus.FAIL, "NO_ENTRY_FOUND");
        }
        long selectedRandomOrderValue = 0l;

        if (randomOrderSeqValue % 10 == 0) {
            selectedRandomOrderValue = minRandomOrderValue + (long) (Math.random() * (randomOrderSeqValue - minRandomOrderValue));
        } else if (randomOrderSeqValue % 5 == 0) {
            selectedRandomOrderValue = minRandomOrderValue + (long) (Math.random() * ((randomOrderSeqValue - minRandomOrderValue) / 3));
        } else if (randomOrderSeqValue % 3 == 0) {
            selectedRandomOrderValue = minRandomOrderValue + (long) (Math.random() * ((randomOrderSeqValue - minRandomOrderValue) / 5));
        } else {
            selectedRandomOrderValue = minRandomOrderValue + (long) (Math.random() * ((10)));
        }

        List<Entry> entries = entryRepo.findByRandomOrder(userUUId, selectedRandomOrderValue);
        if (CollectionUtils.isEmpty(entries)) {
            entries = entryRepo.findByRandomOrderLimit(userUUId, minRandomOrderValue, selectedRandomOrderValue, donePracticing);
        }
        Entry entry = entries.get(entries.size() / 2);

        entry.setRandomOrder(randomOrderSeqValue);
        entryRepo.save(entry);

        List<Usage> usageList = usageRepo.findByEntry(entry);
        entry.setUsageList(usageList);

        return result.setData(entry);
    }

    @Transactional
    @Override
    public Result updatePractice(Long entryId, Boolean donePracticing) {
        entryRepo.updateDonePracticing(entryId, donePracticing);
        return new Result().setStatus(ResultStatus.OK);
    }


    @Override
    public ResultBean<List<Entry>> findEntryByWord(String userUUId, String word) {
        ResultBean<List<Entry>> resultBean = new ResultBean<>();
        if (!ObjectUtils.isEmpty(word) && !ObjectUtils.isEmpty(word.trim())) {
            List<Entry> entriesWithMeaningList = entryRepo.findEntryByUserUUIdAndWordLike(userUUId, word.toUpperCase());

            for (Entry entry : entriesWithMeaningList) {
                List<Usage> usageList = usageRepo.findByEntry(entry);
                entry.setUsageList(usageList);
            }
            resultBean.setData(entriesWithMeaningList);
        } else {
            resultBean.setErrorCode("INVALID WORD!");
        }
        return resultBean;
    }
}
