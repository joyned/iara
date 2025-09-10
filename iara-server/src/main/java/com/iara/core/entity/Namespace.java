package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Table
@Entity
@Getter
@Setter
public class Namespace implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String name;

    @Column
    private String description;

    @Transient
    private List<Environment> environments;

    public void addEnvironment(Environment environment) {
        if (Objects.isNull(this.environments)) {
            this.environments = new LinkedList<>();
        }
        this.environments.add(environment);
    }
}
