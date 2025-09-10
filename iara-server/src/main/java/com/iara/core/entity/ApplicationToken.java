package com.iara.core.entity;

import com.iara.core.entity.converter.DateConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Table(name = "APPLICATION_TOKEN")
@Entity
@Getter
@Setter
public class ApplicationToken implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String name;

    @Column
    private String token;

    @Convert(converter = DateConverter.class)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Convert(converter = DateConverter.class)
    @Column(name = "expires_at")
    private Date expiresAt;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private Policy policy;
}
