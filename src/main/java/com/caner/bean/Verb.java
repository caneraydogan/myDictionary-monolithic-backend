package com.caner.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
public class Verb extends IdObject implements Serializable {

    @Column(length = 32)
    private String word;

    @JoinColumn(name = "USER_ID")
    @JsonBackReference("user")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private boolean donePracticing;

    @JsonManagedReference("verb")
    @OneToMany(mappedBy = "verb", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Meaning> meaningList;

    @JsonManagedReference("verb")
    @OneToMany(mappedBy = "verb", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Usage> usageList;

    private boolean reflexiv;

    @Column(length = 32)
    private String nomen;

}
