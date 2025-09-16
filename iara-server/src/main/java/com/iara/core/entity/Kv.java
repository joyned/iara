package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Table(name = "KEY_VALUE")
@Entity
@Getter
@Setter
public class Kv implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "\"key\"")
    private String key;

    @Column(name = "\"value\"")
    private String value;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @ManyToOne
    @JoinColumn(name = "namespace_id")
    private Namespace namespace;

}
