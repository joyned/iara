package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.NumericBooleanConverter;

import java.io.Serializable;

@Table(name = "APPLICATION_PARAMS")
@Entity
@Getter
@Setter
public class ApplicationParams implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column
    private String key;

    @Column
    private String value;

    @Column
    @Convert(converter = NumericBooleanConverter.class)
    private Boolean secure;
}
