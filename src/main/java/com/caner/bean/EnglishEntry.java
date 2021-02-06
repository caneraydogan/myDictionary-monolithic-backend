package com.caner.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class EnglishEntry extends CommonEntryAttributes implements Serializable {

    @JoinColumn(name = "USER_ID")
    @JsonBackReference("user")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonManagedReference("entry")
    @OneToMany(mappedBy = "entry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnglishMeaning> meaningList;

    @JsonManagedReference("entry")
    @OneToMany(mappedBy = "entry", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EnglishUsage> usageList;
}
