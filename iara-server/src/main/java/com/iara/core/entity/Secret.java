package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table
@Entity
@Getter
@Setter
public class Secret implements Serializable {

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

    @OneToMany(mappedBy = "secret", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @OrderBy("version DESC")
    @ElementCollection
    private Set<SecretVersion> versions = new HashSet<>();
}
