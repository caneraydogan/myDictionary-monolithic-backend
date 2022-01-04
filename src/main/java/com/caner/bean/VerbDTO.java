package com.caner.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VerbDTO {

    private long id;
    private String word;
    private boolean donePracticing;
    private String userUUId;
    private List<Meaning> meaningList;
    private List<Usage> usageList;
    private String reflexiv;
    private String nomen;
}
