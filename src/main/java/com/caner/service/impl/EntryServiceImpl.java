package com.caner.service.impl;

import com.caner.bean.*;
import com.caner.dao.EntryRepo;
import com.caner.dao.MeaningRepo;
import com.caner.dao.UsageRepo;
import com.caner.dao.UserRepo;
import com.caner.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryServiceImpl implements EntryService {

    private static final boolean stillPractising = false;
    private static final boolean donePractising = true;

    private EntryRepo entryRepo;
    private UsageRepo usageRepo;
    private MeaningRepo meaningRepo;
    private UserRepo userRepo;

    // UserUUID -> false=stillPractising/true=donePractising -> EntryList
    private static final HashMap<String, HashMap<Boolean, HashMap<String, Entry>>> entryMap = new HashMap<>();
    private static final HashMap<String, HashMap<Boolean, List<Entry>>> practiceMap = new HashMap<>();

    @Autowired
    public EntryServiceImpl(EntryRepo entryRepo, UsageRepo usageRepo, MeaningRepo meaningRepo, UserRepo userRepo) {
        this.entryRepo = entryRepo;
        this.usageRepo = usageRepo;
        this.meaningRepo = meaningRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ResultBean<Entry> findEntry(String userUUId, String word, Boolean donePracticing) {
        ResultBean<Entry> result = new ResultBean<>(ResultStatus.OK);

        initializeStaticEntryMapForUser(userUUId);
        Entry entry = entryMap.get(userUUId).get(donePracticing).get(word);

        if (entry == null) {
            result.setStatus(ResultStatus.FAIL).setErrorCode("ENTRY_NOT_FOUND");
        } else {
            result.setData(entry);
        }

        return result;
    }

    @Override
    public Result deleteEntry(Long entryId) {
        Entry entry = entryRepo.findEntryById(entryId);
        removeEntryFromMap(entry);

        entryRepo.deleteById(entryId);
        return new Result().setStatus(ResultStatus.OK);
    }

    private void removeEntryFromMap(Entry entry) {
        entryMap.get(entry.getUser().getUserUUId()).get(entry.isDonePracticing()).remove(entry.getWord());
    }

    public ResultBean<List<Entry>> findAll(String userUUId, Boolean donePracticing) {
        initializeStaticEntryMapForUser(userUUId);
        List<Entry> entryList = new ArrayList<>(entryMap.get(userUUId).get(donePracticing).values());

        return new ResultBean<>(entryList);
    }

    public void initializeStaticEntryMapForUser(String userUUId) {
        if (!isUserAlreadyInitializedForEntryMap(userUUId)) {
            entryMap.put(userUUId, new HashMap<>());
            entryMap.get(userUUId).put(donePractising, new HashMap<>());
            entryMap.get(userUUId).put(stillPractising, new HashMap<>());

            List<Entry> entryList = getAllByUUIDandPractising(userUUId, donePractising);
            entryList.forEach(entry -> entryMap.get(userUUId).get(donePractising).put(entry.getWord(), entry));

            entryList = getAllByUUIDandPractising(userUUId, stillPractising);
            entryList.forEach(entry -> entryMap.get(userUUId).get(stillPractising).put(entry.getWord(), entry));
        }
    }

    public void initializeStaticPracticeMapForUser(String userUUId) {
        if (!isUserAlreadyInitializedForPracticeMap(userUUId)) {
            practiceMap.put(userUUId, new HashMap<>());
            practiceMap.get(userUUId).put(donePractising, new ArrayList<>());
            practiceMap.get(userUUId).put(stillPractising, new ArrayList<>());

            List<Entry> entryList = getAllByUUIDandPractising(userUUId, donePractising);
            Collections.shuffle(entryList);
            entryList.forEach(entry -> practiceMap.get(userUUId).get(donePractising).add(entry));


            entryList = getAllByUUIDandPractising(userUUId, stillPractising);
            Collections.shuffle(entryList);
            entryList.forEach(entry -> practiceMap.get(userUUId).get(stillPractising).add(entry));
        }
    }

    private boolean isUserAlreadyInitializedForEntryMap(String userUUId) {
        return entryMap.get(userUUId) != null;
    }

    private boolean isUserAlreadyInitializedForPracticeMap(String userUUId) {
        return practiceMap.get(userUUId) != null;
    }

    private List<Entry> getAllByUUIDandPractising(String userUUId, Boolean donePracticing) {
        List<Entry> entriesWithMeaningList = entryRepo.findAllByUserUUIdWithMeaningList(userUUId, donePracticing);

        for (Entry entry : entriesWithMeaningList) {
            List<Usage> usageList = usageRepo.findByEntry(entry);
            entry.setUsageList(usageList);
        }
        return entriesWithMeaningList;
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
            entryRepo.save(entry);

            List<Meaning> meanings = new ArrayList<>();
            for (Meaning meaning : entryDTO.getMeaningList()) {
                if (!ObjectUtils.isEmpty(meaning.getValue())) {
                    Meaning meaningToBeSaved = new Meaning();
                    meaningToBeSaved.setEntry(entry);
                    meaningToBeSaved.setValue(meaning.getValue());
                    meaningRepo.save(meaningToBeSaved);
                    meanings.add(meaningToBeSaved);
                }
            }

            List<Usage> usages = new ArrayList<>();
            for (Usage usage : entryDTO.getUsageList()) {
                if (!ObjectUtils.isEmpty(usage.getValue())) {
                    Usage usageToBeSaved = new Usage();
                    usageToBeSaved.setEntry(entry);
                    usageToBeSaved.setValue(usage.getValue());
                    usageRepo.save(usageToBeSaved);
                    usages.add(usageToBeSaved);
                }
            }

            synchronizeMapForUser(entryDTO, entry, meanings, usages);
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


            List<Meaning> meanings = new ArrayList<>();
            List<Usage> usages = new ArrayList<>();

            List<Meaning> toBeDeletedMeanings = new ArrayList<>();
            for (Meaning meaningDB : entry.getMeaningList()) {
                boolean shouldMeaningBeDeleted = true;
                for (Meaning meaningDTO : entryDTO.getMeaningList()) {
                    if (meaningDB.getId() == meaningDTO.getId()) {
                        meaningDB.setValue(meaningDTO.getValue());
                        shouldMeaningBeDeleted = false;
                        meaningRepo.save(meaningDB);
                        meanings.add(meaningDB);
                        break;
                    }
                }
                if (shouldMeaningBeDeleted) {
                    toBeDeletedMeanings.add(meaningDB);
                }
            }

            for (Meaning meaning : entryDTO.getMeaningList()) {
                if (meaning.getId() == null) {
                    Meaning meaningToBeSaved = new Meaning();
                    meaningToBeSaved.setEntry(entry);
                    meaningToBeSaved.setValue(meaning.getValue());
                    meaningRepo.save(meaningToBeSaved);
                    meanings.add(meaningToBeSaved);
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
                        usages.add(usageDB);
                        break;
                    }
                }
                if (shouldUsageBeDeleted) {
                    toBeDeletedUsages.add(usageDB);
                }
            }

            for (Usage usage : entryDTO.getUsageList()) {
                if (usage.getId() == null) {
                    Usage usageToBeSaved = new Usage();
                    usageToBeSaved.setEntry(entry);
                    usageToBeSaved.setValue(usage.getValue());
                    usageRepo.save(usageToBeSaved);
                    usages.add(usageToBeSaved);
                }
            }

            meaningRepo.deleteInBatch(toBeDeletedMeanings);
            usageRepo.deleteInBatch(toBeDeletedUsages);

            insertIntoMap(entryDTO, entry, meanings, usages);
        }
        return result;
    }

    private void synchronizeMapForUser(EntryDTO entryDTO, Entry entry, List<Meaning> meanings, List<Usage> usages) {
        if (isUserAlreadyInitializedForEntryMap(entryDTO.getUserUUId())) {
            insertIntoMap(entryDTO, entry, meanings, usages);
        } else {
            initializeStaticEntryMapForUser(entryDTO.getUserUUId());
        }
    }

    private void insertIntoMap(EntryDTO entryDTO, Entry entry, List<Meaning> meanings, List<Usage> usages) {
        entry.setMeaningList(meanings);
        entry.setUsageList(usages);
        entryMap.get(entryDTO.getUserUUId()).get(entryDTO.isDonePracticing()).put(entryDTO.getWord(), entry);
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


    public ResultBean<Entry> findRandomEntry(String userUUId, Boolean donePractising) {
        ResultBean<Entry> result = new ResultBean<>(ResultStatus.OK);

        initializeStaticPracticeMapForUser(userUUId);

        int listSize = practiceMap.get(userUUId).get(donePractising).size();

        if (listSize == 0) {
            List<Entry> entryList = getAllByUUIDandPractising(userUUId, donePractising);
            Collections.shuffle(entryList);
            entryList.forEach(entry -> practiceMap.get(userUUId).get(donePractising).add(entry));
        }

        listSize = practiceMap.get(userUUId).get(donePractising).size();
        if (listSize == 0) {
            return result.setResultCodeAndMessage(ResultStatus.FAIL, "NO_ENTRY_FOUND");
        }

        Entry entry = practiceMap.get(userUUId).get(donePractising).get(listSize - 1);
        practiceMap.get(userUUId).get(donePractising).remove(entry);

        return result.setData(entry);
    }

    @Transactional
    @Override
    public Result updatePractice(EntryDTO entryDTO, Boolean practiceValue) {
        entryRepo.updateDonePracticing(entryDTO.getId(), practiceValue);

        Entry entry = entryMap.get(entryDTO.getUserUUId()).get(entryDTO.isDonePracticing()).get(entryDTO.getWord());

        entryMap.get(entryDTO.getUserUUId()).get(!practiceValue).remove(entryDTO.getWord());
        entryMap.get(entryDTO.getUserUUId()).get(practiceValue).put(entry.getWord(), entry);

        return new Result().setStatus(ResultStatus.OK);
    }


    @Override
    public ResultBean<List<Entry>> findEntryByWord(String userUUId, String word) {
        ResultBean<List<Entry>> resultBean = new ResultBean<>();

        if (!ObjectUtils.isEmpty(word) && !ObjectUtils.isEmpty(word.trim())) {

            List<Entry> entryList = entryMap.get(userUUId).get(stillPractising).values()
                    .stream()
                    .filter(entry -> entry.getWord().toLowerCase().contains(word.toLowerCase()))
                    .collect(Collectors.toList());

            entryList.addAll(entryMap.get(userUUId).get(donePractising).values()
                    .stream()
                    .filter(entry -> entry.getWord().toLowerCase().contains(word.toLowerCase()))
                    .collect(Collectors.toList()));

            resultBean.setData(entryList);
        } else {
            resultBean.setErrorCode("INVALID WORD!");
        }
        return resultBean;
    }
}
