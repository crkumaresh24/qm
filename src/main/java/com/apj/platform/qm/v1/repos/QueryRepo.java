package com.apj.platform.qm.v1.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apj.platform.qm.v1.entities.QueryMetadata;

@Repository
public interface QueryRepo extends JpaRepository<QueryMetadata, Long> {
}
