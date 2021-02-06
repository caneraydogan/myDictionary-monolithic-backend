package com.caner.controller;


import com.caner.bean.EnglishEntry;
import com.caner.bean.EnglishEntryDTO;
import com.caner.bean.Result;
import com.caner.bean.ResultBean;
import com.caner.service.EnglishEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/english")
public class EnglishEntryController {

    private EnglishEntryService entryService;

    @Autowired
    public EnglishEntryController(EnglishEntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    @RequestMapping(value = "/findEntry/{id}")
    public ResultBean<EnglishEntry> findEntry(@PathVariable("id") Long entryId) {
        return entryService.findEntry(entryId);
    }

    @GetMapping
    @RequestMapping(value = "/findEntryByWord/{userUUId}/{word}")
    public ResultBean<List<EnglishEntry>> findEntryByWord(@PathVariable String userUUId, @PathVariable("word") String word) {
        return entryService.findEntryByWord(userUUId, word);
    }

    @DeleteMapping
    @RequestMapping(value = "/deleteEntry/{id}")
    public Result deleteEntry(@PathVariable("id") Long entryId) {
        return entryService.deleteEntry(entryId);
    }

    @GetMapping
    @RequestMapping(value = "/findRandomEntry/{userUUId}/{donePracticing}")
    public ResultBean<EnglishEntry> findRandomEntry(@PathVariable String userUUId, @PathVariable("donePracticing") boolean donePracticing) {
        return entryService.findRandomEntry(userUUId, donePracticing);
    }

    @GetMapping
    @RequestMapping(value = "/findAll/{userUUId}/{donePracticing}")
    public ResultBean<List<EnglishEntry>> findAll(@PathVariable String userUUId, @PathVariable("donePracticing") boolean donePracticing) {
        return entryService.findAll(userUUId, donePracticing);
    }

    @PutMapping
    @RequestMapping(value = "/saveEntry")
    public Result saveEntry(@RequestBody EnglishEntryDTO entryDTO) {
        return entryService.saveEntry(entryDTO);
    }

    @PostMapping
    @RequestMapping(value = "/updateEntry")
    public Result updateEntry(@RequestBody EnglishEntryDTO entryDTO) {
        return entryService.updateEntry(entryDTO);
    }

    @PostMapping
    @RequestMapping(value = "/updatePractice/{entryId}/{donePracticing}")
    public Result updatePractice(@PathVariable("entryId") Long entryId, @PathVariable("donePracticing") boolean donePracticing) {
        return entryService.updatePractice(entryId, donePracticing);
    }
}
