package com.caner.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
public class Practice extends IdObject implements Serializable {

    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    private Long lastPracticedIndex;
}
