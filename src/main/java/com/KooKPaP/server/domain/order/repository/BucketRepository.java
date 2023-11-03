package com.KooKPaP.server.domain.order.repository;


import com.KooKPaP.server.domain.order.entity.Bucket;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BucketRepository extends JpaRepository<Bucket, Long> {

    List<Bucket> findBucketsById(Long id);


    Optional<Bucket> findBucketByBucketId(Long bucketId);

}
