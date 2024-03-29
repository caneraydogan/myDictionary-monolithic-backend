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
public class Entry extends IdObject implements Serializable {

    @Column(length = 32)
    private String word;

    @Column(length = 3)
    private String artikel;

    @JoinColumn(name = "USER_ID")
    @JsonBackReference("user")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private boolean donePracticing;

    @JsonManagedReference("entry")
    @OneToMany(mappedBy = "entry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Meaning> meaningList;

    @JsonManagedReference("entry")
    @OneToMany(mappedBy = "entry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Usage> usageList;

}
