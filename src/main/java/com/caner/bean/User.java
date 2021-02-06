package com.caner.bean;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends IdObject {

    private static final long serialVersionUID = -6839787831631450288L;

    @Column(name = "USER_UUID", nullable = false, unique = true)
    private String userUUId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    @JsonManagedReference("user")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<GermanEntry> entryList;

}
