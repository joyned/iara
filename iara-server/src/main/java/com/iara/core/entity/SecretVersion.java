package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;

@Table
@Entity
@Getter
@Setter
public class SecretVersion implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "\"value\"")
    private String value;

    @Column
    private Integer version;

    @Column
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean disabled;

    @Column
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean destroyed;

    @ManyToOne
    @JoinColumn(name = "secret_id")
    private Secret secret;

}
