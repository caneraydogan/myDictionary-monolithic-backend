package com.caner.bean;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public class CommonEntryAttributes extends IdObject implements Serializable {
    @Column(length = 32)
    private String word;

    private Long randomOrder;

    private boolean donePracticing;
}
