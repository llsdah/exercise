package com.example.scheduler.dependency.infra.persistence;
import jakarta.persistence.*;
@Entity @Table(name = "BATCH_DEPENDENCY_GRAPH_LOCK")
public class DependencyGraphLock {
    @Id @Column(name = "LOCK_ID", length = 20) private String id;
    protected DependencyGraphLock() { }
    DependencyGraphLock(String id) { this.id = id; }
}
