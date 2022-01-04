package com.caner.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
public class Past extends IdObject implements Serializable {

    @JsonBackReference("verb")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VERB_ID")
    private Verb verb;

    private String perfekt;

    private String prateritum;

    private boolean isSein;
}
