package com.caner.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnglishEntryDTO {
    private long id;
    private String word;
    private boolean donePracticing;
    private String userUUId;
    private List<EnglishMeaning> meaningList;
    private List<EnglishUsage> usageList;
}
