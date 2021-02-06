package com.caner.controller;

import com.caner.bean.*;
import com.caner.service.GermanEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/german")
public class GermanEntryController {

    private GermanEntryService germanEntryService;

    @Autowired
    public GermanEntryController(GermanEntryService germanEntryService) {
        this.germanEntryService = germanEntryService;
    }

    @GetMapping
    @RequestMapping(value = "/findEntry/{id}")
    public ResultBean<GermanEntry> findEntry(@PathVariable("id") Long entryId) {
        return germanEntryService.findEntry(entryId);
    }

    @GetMapping
    @RequestMapping(value = "/findEntryByWord/{userUUId}/{word}")
    public ResultBean<List<GermanEntry>> findEntryByWord(@PathVariable String userUUId, @PathVariable("word") String word) {
        return germanEntryService.findEntryByWord(userUUId, word);
    }

    @DeleteMapping
    @RequestMapping(value = "/deleteEntry/{id}")
    public Result deleteEntry(@PathVariable("id") Long entryId) {
        return germanEntryService.deleteEntry(entryId);
    }

    @GetMapping
    @RequestMapping(value = "/findRandomEntry/{userUUId}/{donePracticing}")
    public ResultBean<GermanEntry> findRandomEntry(@PathVariable String userUUId, @PathVariable("donePracticing") boolean donePracticing) {
        return germanEntryService.findRandomEntry(userUUId, donePracticing);
    }

    @GetMapping
    @RequestMapping(value = "/findAll/{userUUId}/{donePracticing}")
    public ResultBean<List<GermanEntry>> findAll(@PathVariable String userUUId, @PathVariable("donePracticing") boolean donePracticing) {
        return germanEntryService.findAll(userUUId, donePracticing);
    }

    @PutMapping
    @RequestMapping(value = "/saveEntry")
    public Result saveEntry(@RequestBody GermanEntryDTO germanEntryDTO) {
        return germanEntryService.saveEntry(germanEntryDTO);
    }

    @PostMapping
    @RequestMapping(value = "/updateEntry")
    public Result updateEntry(@RequestBody GermanEntryDTO germanEntryDTO) {
        return germanEntryService.updateEntry(germanEntryDTO);
    }

    @PostMapping
    @RequestMapping(value = "/updatePractice/{entryId}/{donePracticing}")
    public Result updatePractice(@PathVariable("entryId") Long entryId, @PathVariable("donePracticing") boolean donePracticing) {
        return germanEntryService.updatePractice(entryId, donePracticing);
    }
}
