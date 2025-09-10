package com.iara.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Table
@Entity
@Getter
@Setter
public class Policy implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String rule;

}
