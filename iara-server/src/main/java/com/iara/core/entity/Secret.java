package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Table
@Entity
@Getter
@Setter
public class Secret {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "environment_id")
    private Environment environment;

    @ManyToOne
    @JoinColumn(name = "namespace_id")
    private Namespace namespace;

    @OneToMany(mappedBy = "secret")
    @OrderBy("version DESC")
    private List<SecretVersion> versions;
}
