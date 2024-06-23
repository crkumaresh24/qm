package com.apj.platform.qm.v1.entities;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "queries")
@EntityListeners(AuditingEntityListener.class)
public class QueryMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_queries")
    @SequenceGenerator(name = "seq_queries", allocationSize = 1)
    private Long id;
    private String name;
    @Lob
    @Column(length = 65536)
    private String query;
    private boolean isSelectQuery;
    private boolean containsSysParams;
    private boolean containsReqParams;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String modifiedBy;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date modifiedAt;
}
