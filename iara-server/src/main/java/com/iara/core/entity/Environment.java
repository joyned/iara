package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Table
@Entity
@Getter
@Setter
public class Environment implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String name;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "namespace_id")
    private Namespace namespace;

}
