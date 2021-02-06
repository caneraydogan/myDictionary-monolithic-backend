package com.caner.bean;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EnableJpaAuditing
@Getter
@Setter
public class IdObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
//
//    @CreatedBy
//    @JsonIgnore
//    private String createdBy;
//
//    @LastModifiedBy
//    @JsonIgnore
//    private String updatedBy;
//
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @JsonIgnore
//    private Date createdOn;
//
//    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    @JsonIgnore
//    private Date updatedOn;
//
//    @Column(name = "ROW_STATUS", columnDefinition = "varchar(20) default 'ACTIVE'", insertable = false)
//    @Enumerated(EnumType.STRING)
//    @JsonIgnore
//    private RowStatus rowStatus;
//
//    @Version
//    @JsonIgnore
//    @Column(name = "ROW_VERSION", columnDefinition = "int default 0", insertable = false)
//    private int rowVersion;
}
