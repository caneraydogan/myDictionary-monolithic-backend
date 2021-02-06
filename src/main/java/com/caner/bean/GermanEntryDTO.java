package com.caner.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GermanEntryDTO {

    private long id;
    private String word;
    private boolean donePracticing;
    private String userUUId;
    private List<GermanMeaning> meaningList;
    private List<GermanUsage> usageList;
    private String artikel;
}
