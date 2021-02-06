package com.caner.service.impl;

import com.caner.bean.*;
import com.caner.dao.EnglishEntryRepo;
import com.caner.dao.EnglishMeaningRepo;
import com.caner.dao.EnglishUsageRepo;
import com.caner.dao.UserRepo;
import com.caner.service.EnglishEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnglishEntryServiceImpl implements EnglishEntryService {

    private EnglishEntryRepo entryRepo;
    private EnglishUsageRepo usageRepo;
    private EnglishMeaningRepo meaningRepo;
    private UserRepo userRepo;

    @Autowired
    public EnglishEntryServiceImpl(EnglishEntryRepo entryRepo, EnglishUsageRepo usageRepo, EnglishMeaningRepo meaningRepo, UserRepo userRepo) {
        this.entryRepo = entryRepo;
        this.usageRepo = usageRepo;
        this.meaningRepo = meaningRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ResultBean<EnglishEntry> findEntry(Long entryId) {

        ResultBean<EnglishEntry> result = new ResultBean<>(ResultStatus.OK);

        EnglishEntry entry = entryRepo.findEntryById(entryId);

        if (entry == null) {
            result.setStatus(ResultStatus.FAIL).setErrorCode("ENTRY_NOT_FOUND");
        } else {
            List<EnglishUsage> usageList = usageRepo.findByEntry(entry);
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

    public ResultBean<List<EnglishEntry>> findAll(String userUUId, Boolean donePracticing) {
        ResultBean<List<EnglishEntry>> resultBean = new ResultBean<>();
        List<EnglishEntry> entriesWithMeaningList = entryRepo.findAllByUserUUIdWithMeaningList(userUUId, donePracticing);

        for (EnglishEntry entry : entriesWithMeaningList) {
            List<EnglishUsage> usageList = usageRepo.findByEntry(entry);
            entry.setUsageList(usageList);
        }
        resultBean.setData(entriesWithMeaningList);
        return resultBean;
    }

    @Override
    public Result saveEntry(EnglishEntryDTO entryDTO) {
        Result result = validateEntry(entryDTO);

        if (result.isOk()) {
            User user = userRepo.findByUserUUId(entryDTO.getUserUUId());

            if (user == null) {
                return result.setStatus(ResultStatus.FAIL).setMessage("INVALID_USER");
            }

            EnglishEntry entry = new EnglishEntry();
            entry.setUser(user);
            entry.setWord(entryDTO.getWord());
            entry.setDonePracticing(entryDTO.isDonePracticing());
            entry.setRandomOrder(0L);
            entryRepo.save(entry);

            for (EnglishMeaning meaning : entryDTO.getMeaningList()) {
                if (!ObjectUtils.isEmpty(meaning.getValue())) {
                    EnglishMeaning meaning1 = new EnglishMeaning();
                    meaning1.setEntry(entry);
                    meaning1.setValue(meaning.getValue());
                    meaningRepo.save(meaning1);
                }
            }

            for (EnglishUsage usage : entryDTO.getUsageList()) {
                if (!ObjectUtils.isEmpty(usage.getValue())) {
                    EnglishUsage usage1 = new EnglishUsage();
                    usage1.setEntry(entry);
                    usage1.setValue(usage.getValue());
                    usageRepo.save(usage1);
                }
            }

        }
        return result;
    }

    @Override
    public Result updateEntry(EnglishEntryDTO entryDTO) {
        Result result = validateEntry(entryDTO);

        if (result.isOk()) {
            EnglishEntry entry = entryRepo.findEntryById(entryDTO.getId());
            entry.setDonePracticing(entryDTO.isDonePracticing());
            entry.setWord(entryDTO.getWord());
            entryRepo.save(entry);

            List<EnglishMeaning> toBeDeletedMeanings = new ArrayList<>();
            for (EnglishMeaning meaningDB : entry.getMeaningList()) {
                boolean shouldMeaningBeDeleted = true;
                for (EnglishMeaning meaningDTO : entryDTO.getMeaningList()) {
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


            for (EnglishMeaning meaning : entryDTO.getMeaningList()) {
                if (meaning.getId() == null) {
                    EnglishMeaning meaning1 = new EnglishMeaning();
                    meaning1.setEntry(entry);
                    meaning1.setValue(meaning.getValue());
                    meaningRepo.save(meaning1);
                }
            }

            List<EnglishUsage> toBeDeletedUsages = new ArrayList<>();
            for (EnglishUsage usageDB : entry.getUsageList()) {
                boolean shouldUsageBeDeleted = true;
                for (EnglishUsage usageDTO : entryDTO.getUsageList()) {
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

            for (EnglishUsage usage : entryDTO.getUsageList()) {
                if (usage.getId() == null) {
                    EnglishUsage usage1 = new EnglishUsage();
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

    public Result validateEntry(EnglishEntryDTO entryDTO) {
        Result result = new Result().setStatus(ResultStatus.FAIL);

        if (entryDTO.getUserUUId() == null) {
            result.setErrorCode("MISSING_USER");
        } else if (entryDTO.getWord() == null) {
            result.setErrorCode("MISSING_WORD");
        } else if (entryDTO.getMeaningList().isEmpty()) {
            result.setErrorCode("MISSING_MEANING");
        } else {
            EnglishEntry entry = entryRepo.findEntryByWord(entryDTO.getWord());
            if (entry != null && entry.getId() != entryDTO.getId()) {
                result.setErrorCode("WORD_ALREADY_EXISTS");
            }
        }

        return result.getErrorCode() == null ? result.setStatus(ResultStatus.OK) : result;
    }


    public ResultBean<EnglishEntry> findRandomEntry(String userUUId, Boolean donePracticing) {
        ResultBean<EnglishEntry> result = new ResultBean<>(ResultStatus.OK);

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

        List<EnglishEntry> entries = entryRepo.findByRandomOrder(userUUId, selectedRandomOrderValue);
        if (CollectionUtils.isEmpty(entries)) {
            entries = entryRepo.findByRandomOrderLimit(userUUId, minRandomOrderValue, selectedRandomOrderValue, donePracticing);
        }
        EnglishEntry entry = entries.get(entries.size() / 2);

        entry.setRandomOrder(randomOrderSeqValue);
        entryRepo.save(entry);

        List<EnglishUsage> usageList = usageRepo.findByEntry(entry);
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
    public ResultBean<List<EnglishEntry>> findEntryByWord(String userUUId, String word) {
        ResultBean<List<EnglishEntry>> resultBean = new ResultBean<>();
        if (!ObjectUtils.isEmpty(word) && !ObjectUtils.isEmpty(word.trim())) {
            List<EnglishEntry> entriesWithMeaningList = entryRepo.findEntryByUserUUIdAndWordLike(userUUId, word.toUpperCase());

            for (EnglishEntry entry : entriesWithMeaningList) {
                List<EnglishUsage> usageList = usageRepo.findByEntry(entry);
                entry.setUsageList(usageList);
            }
            resultBean.setData(entriesWithMeaningList);
        } else {
            resultBean.setErrorCode("INVALID WORD!");
        }
        return resultBean;
    }
}
