package com.caner.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Invitation extends IdObject {

    private String code;

    @JoinColumn(name = "USER_ID")
    @JsonBackReference("user")
    @ManyToOne(fetch = FetchType.LAZY)
    private User usedBy;
}
