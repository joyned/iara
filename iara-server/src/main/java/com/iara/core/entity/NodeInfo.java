package com.iara.core.entity;

import com.iara.core.model.NodeRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "node_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfo implements Serializable {

    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private int port;

    @Column(name = "last_heartbeat")
    private Date lastHeartbeat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NodeRole role;

    @Column(nullable = false)
    private long term;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    public NodeInfo(String id, String host, int port, NodeRole role, long term) {
        this();
        this.id = id;
        this.host = host;
        this.port = port;
        this.role = role;
        this.term = term;
        this.lastHeartbeat = new Date();
    }

}
