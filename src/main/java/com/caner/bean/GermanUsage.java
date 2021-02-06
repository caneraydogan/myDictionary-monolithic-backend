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
public class GermanUsage extends IdObject implements Serializable {

    @JsonBackReference("entry")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTRY_ID")
    private GermanEntry entry;

    private String value;
}
