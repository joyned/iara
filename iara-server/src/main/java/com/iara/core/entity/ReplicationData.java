package com.iara.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "replication_data", indexes = {
        @Index(name = "idx_key", columnList = "\"key\""),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Getter
@Setter
public class ReplicationData implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "\"key\"", nullable = false)
    private String key;

    @Column(name = "\"value\"", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(nullable = false)
    private Date timestamp;

    @Column(nullable = false)
    private long term;

    @Column(name = "committed")
    private boolean committed = false;

    @Column(name = "created_at")
    private Date createdAt;

}
