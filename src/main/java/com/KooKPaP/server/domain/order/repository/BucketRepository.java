package com.KooKPaP.server.domain.order.repository;


import com.KooKPaP.server.domain.order.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketRepository extends JpaRepository<Bucket, Long> {
}
