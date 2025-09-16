package com.iara.core.entity;

import com.iara.core.entity.converter.DateConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Date;

@Table(name = "KEY_VALUE_HISTORY")
@Entity
@Getter
@Setter
public class KvHistory implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @ManyToOne
    @JoinColumn(name = "kv_id")
    private Kv keyValue;

    @Column(name = "\"value\"")
    private String value;

    @Convert(converter = DateConverter.class)
    @Column(name = "updated_at", columnDefinition = "TEXT")
    private Date updatedAt;

    @Column(name = "\"user\"")
    private String user;
}
