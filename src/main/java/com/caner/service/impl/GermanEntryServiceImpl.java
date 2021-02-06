package com.caner.service.impl;

import com.caner.bean.*;
import com.caner.dao.GermanEntryRepo;
import com.caner.dao.GermanMeaningRepo;
import com.caner.dao.GermanUsageRepo;
import com.caner.dao.UserRepo;
import com.caner.service.GermanEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class GermanEntryServiceImpl implements GermanEntryService {

    private GermanEntryRepo germanEntryRepo;
    private GermanUsageRepo germanUsageRepo;
    private GermanMeaningRepo germanMeaningRepo;
    private UserRepo userRepo;

    @Autowired
    public GermanEntryServiceImpl(GermanEntryRepo germanEntryRepo, GermanUsageRepo germanUsageRepo, GermanMeaningRepo germanMeaningRepo, UserRepo userRepo) {
        this.germanEntryRepo = germanEntryRepo;
        this.germanUsageRepo = germanUsageRepo;
        this.germanMeaningRepo = germanMeaningRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ResultBean<GermanEntry> findEntry(Long entryId) {

        ResultBean<GermanEntry> result = new ResultBean<>(ResultStatus.OK);

        GermanEntry entry = germanEntryRepo.findEntryById(entryId);

        if (entry == null) {
            result.setStatus(ResultStatus.FAIL).setErrorCode("ENTRY_NOT_FOUND");
        } else {
            List<GermanUsage> germanUsageList = germanUsageRepo.findByEntry(entry);
            entry.setUsageList(germanUsageList);
            result.setData(entry);
        }

        return result;
    }

    @Override
    public Result deleteEntry(Long entryId) {
        germanEntryRepo.deleteById(entryId);
        return new Result().setStatus(ResultStatus.OK);
    }

    public ResultBean<List<GermanEntry>> findAll(String userUUId, Boolean donePracticing) {
        ResultBean<List<GermanEntry>> resultBean = new ResultBean<>();
        List<GermanEntry> entriesWithMeaningList = germanEntryRepo.findAllByUserUUIdWithMeaningList(userUUId, donePracticing);

        for (GermanEntry entry : entriesWithMeaningList) {
            List<GermanUsage> germanUsageList = germanUsageRepo.findByEntry(entry);
            entry.setUsageList(germanUsageList);
        }
        resultBean.setData(entriesWithMeaningList);
        return resultBean;
    }

    @Override
    public Result saveEntry(GermanEntryDTO germanEntryDTO) {
        Result result = validateEntry(germanEntryDTO);

        if (result.isOk()) {
            User user = userRepo.findByUserUUId(germanEntryDTO.getUserUUId());

            if (user == null) {
                return result.setStatus(ResultStatus.FAIL).setMessage("INVALID_USER");
            }

            GermanEntry entry = new GermanEntry();
            entry.setUser(user);
            entry.setWord(germanEntryDTO.getWord());
            entry.setArtikel(germanEntryDTO.getArtikel());
            entry.setDonePracticing(germanEntryDTO.isDonePracticing());
            entry.setRandomOrder(0L);
            germanEntryRepo.save(entry);

            for (GermanMeaning germanMeaning : germanEntryDTO.getMeaningList()) {
                if (!ObjectUtils.isEmpty(germanMeaning.getValue())) {
                    GermanMeaning germanMeaning1 = new GermanMeaning();
                    germanMeaning1.setEntry(entry);
                    germanMeaning1.setValue(germanMeaning.getValue());
                    germanMeaningRepo.save(germanMeaning1);
                }
            }

            for (GermanUsage germanUsage : germanEntryDTO.getUsageList()) {
                if (!ObjectUtils.isEmpty(germanUsage.getValue())) {
                    GermanUsage germanUsage1 = new GermanUsage();
                    germanUsage1.setEntry(entry);
                    germanUsage1.setValue(germanUsage.getValue());
                    germanUsageRepo.save(germanUsage1);
                }
            }

        }
        return result;
    }

    @Override
    public Result updateEntry(GermanEntryDTO germanEntryDTO) {
        Result result = validateEntry(germanEntryDTO);

        if (result.isOk()) {
            GermanEntry entry = germanEntryRepo.findEntryById(germanEntryDTO.getId());
            entry.setWord(germanEntryDTO.getWord());
            entry.setArtikel(germanEntryDTO.getArtikel());
            entry.setDonePracticing(germanEntryDTO.isDonePracticing());
            germanEntryRepo.saveAndFlush(entry);

            List<GermanMeaning> toBeDeletedGermanMeanings = new ArrayList<>();
            for (GermanMeaning germanMeaningDB : entry.getMeaningList()) {
                boolean shouldMeaningBeDeleted = true;
                for (GermanMeaning germanMeaningDTO : germanEntryDTO.getMeaningList()) {
                    if (germanMeaningDB.getId() == germanMeaningDTO.getId()) {
                        germanMeaningDB.setValue(germanMeaningDTO.getValue());
                        shouldMeaningBeDeleted = false;
                        germanMeaningRepo.save(germanMeaningDB);
                        break;
                    }
                }
                if (shouldMeaningBeDeleted) {
                    toBeDeletedGermanMeanings.add(germanMeaningDB);
                }
            }

            for (GermanMeaning germanMeaning : germanEntryDTO.getMeaningList()) {
                if (germanMeaning.getId() == null) {
                    GermanMeaning germanMeaning1 = new GermanMeaning();
                    germanMeaning1.setEntry(entry);
                    germanMeaning1.setValue(germanMeaning.getValue());
                    germanMeaningRepo.save(germanMeaning1);
                }
            }

            List<GermanUsage> toBeDeletedGermanUsages = new ArrayList<>();
            for (GermanUsage germanUsageDB : entry.getUsageList()) {
                boolean shouldUsageBeDeleted = true;
                for (GermanUsage germanUsageDTO : germanEntryDTO.getUsageList()) {
                    if (germanUsageDB.getId() == germanUsageDTO.getId()) {
                        germanUsageDB.setValue(germanUsageDTO.getValue());
                        shouldUsageBeDeleted = false;
                        germanUsageRepo.save(germanUsageDB);
                        break;
                    }
                }
                if (shouldUsageBeDeleted) {
                    toBeDeletedGermanUsages.add(germanUsageDB);
                }
            }

            for (GermanUsage germanUsage : germanEntryDTO.getUsageList()) {
                if (germanUsage.getId() == null) {
                    GermanUsage germanUsage1 = new GermanUsage();
                    germanUsage1.setEntry(entry);
                    germanUsage1.setValue(germanUsage.getValue());
                    germanUsageRepo.save(germanUsage1);
                }
            }

            germanMeaningRepo.deleteInBatch(toBeDeletedGermanMeanings);
            germanUsageRepo.deleteInBatch(toBeDeletedGermanUsages);

        }
        return result;
    }

    public Result validateEntry(GermanEntryDTO germanEntryDTO) {

        Result result = new Result().setStatus(ResultStatus.FAIL);

        if (germanEntryDTO.getUserUUId() == null) {
            result.setErrorCode("MISSING_USER");
        } else if (germanEntryDTO.getWord() == null) {
            result.setErrorCode("MISSING_WORD");
        } else if (germanEntryDTO.getMeaningList().isEmpty()) {
            result.setErrorCode("MISSING_MEANING");
        } else if (germanEntryDTO.getArtikel() != null && !Artikel.isValid(germanEntryDTO.getArtikel().toUpperCase())) {
            result.setErrorCode("WRONG_ARTIKEL");
        } else {
            GermanEntry entry = germanEntryRepo.findEntryByWord(germanEntryDTO.getWord());
            if (entry != null && entry.getId() != germanEntryDTO.getId()) {
                result.setErrorCode("WORD_ALREADY_EXISTS");
            }
        }

        return result.getErrorCode() == null ? result.setStatus(ResultStatus.OK) : result;
    }


    public ResultBean<GermanEntry> findRandomEntry(String userUUId, Boolean donePracticing) {
        ResultBean<GermanEntry> result = new ResultBean<>(ResultStatus.OK);

        long randomOrderSeqValue = germanEntryRepo.findRandomOrderSeqValue();
        long minRandomOrderValue;
        try {
            minRandomOrderValue = germanEntryRepo.findMinimumRandomOrderEntry(userUUId, donePracticing);
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

        List<GermanEntry> entries = germanEntryRepo.findByRandomOrder(userUUId, selectedRandomOrderValue);
        if (CollectionUtils.isEmpty(entries)) {
            entries = germanEntryRepo.findByRandomOrderLimit(userUUId, minRandomOrderValue, selectedRandomOrderValue, donePracticing);
        }
        GermanEntry entry = entries.get(entries.size() / 2);

        entry.setRandomOrder(randomOrderSeqValue);
        germanEntryRepo.save(entry);

        List<GermanUsage> germanUsageList = germanUsageRepo.findByEntry(entry);
        entry.setUsageList(germanUsageList);

        return result.setData(entry);
    }

    @Transactional
    @Override
    public Result updatePractice(Long entryId, Boolean donePracticing) {
        germanEntryRepo.updateDonePracticing(entryId, donePracticing);
        return new Result().setStatus(ResultStatus.OK);
    }


    @Override
    public ResultBean<List<GermanEntry>> findEntryByWord(String userUUId, String word) {
        ResultBean<List<GermanEntry>> resultBean = new ResultBean<>();
        if (!ObjectUtils.isEmpty(word) && !ObjectUtils.isEmpty(word.trim())) {
            List<GermanEntry> entriesWithMeaningList = germanEntryRepo.findEntryByUserUUIdAndWordLike(userUUId, word.toUpperCase());

            for (GermanEntry entry : entriesWithMeaningList) {
                List<GermanUsage> germanUsageList = germanUsageRepo.findByEntry(entry);
                entry.setUsageList(germanUsageList);
            }
            resultBean.setData(entriesWithMeaningList);
        } else {
            resultBean.setErrorCode("INVALID WORD!");
        }
        return resultBean;
    }
}
