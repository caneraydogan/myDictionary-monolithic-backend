package com.caner.controller;

import com.caner.bean.*;
import com.caner.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EntryController {

    private EntryService entryService;

    @Autowired
    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    @RequestMapping(value = "/findEntry/{userUUId}/{word}/{donePracticing}")
    public ResultBean<Entry> findEntry(@PathVariable String userUUId, @PathVariable String word, @PathVariable boolean donePracticing) {
        return entryService.findEntry(userUUId, word, donePracticing);
    }

    @GetMapping
    @RequestMapping(value = "/findEntryByWord/{userUUId}/{word}")
    public ResultBean<List<Entry>> findEntryByWord(@PathVariable String userUUId, @PathVariable String word) {
        return entryService.findEntryByWord(userUUId, word);
    }

    @DeleteMapping
    @RequestMapping(value = "/deleteEntry/{id}")
    public Result deleteEntry(@PathVariable("id") Long entryId) {
        return entryService.deleteEntry(entryId);
    }

    @GetMapping
    @RequestMapping(value = "/findRandomEntry/{userUUId}/{donePracticing}")
    public ResultBean<Entry> findRandomEntry(@PathVariable String userUUId, @PathVariable boolean donePracticing) {
        return entryService.findRandomEntry(userUUId, donePracticing);
    }

    @GetMapping
    @RequestMapping(value = "/findAll/{userUUId}/{donePracticing}")
    public ResultBean<List<Entry>> findAll(@PathVariable String userUUId, @PathVariable boolean donePracticing) {
        return entryService.findAll(userUUId, donePracticing);
    }

    @PutMapping
    @RequestMapping(value = "/saveEntry")
    public Result saveEntry(@RequestBody EntryDTO entryDTO) {
        return entryService.saveEntry(entryDTO);
    }

    @PostMapping
    @RequestMapping(value = "/updateEntry")
    public Result updateEntry(@RequestBody EntryDTO entryDTO) {
        return entryService.updateEntry(entryDTO);
    }

    @PostMapping
    @RequestMapping(value = "/updatePractice/{practiceValue}")
    public Result updatePractice(@PathVariable boolean practiceValue, @RequestBody EntryDTO entryDTO) {
        return entryService.updatePractice(entryDTO, practiceValue);
    }
}
