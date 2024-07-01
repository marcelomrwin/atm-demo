package com.redhat.atm.repository;

import com.redhat.atm.model.Subscription;
import com.redhat.atm.model.ed254.TopicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    @Query("SELECT s from Subscription s JOIN s.topicsOfInterest t WHERE t = :t order by s.createdAt")
    List<Subscription> findByTopicsOfInterestOrderByCreatedAt(@Param("t") TopicType t);

    @Query("SELECT s from Subscription s JOIN s.topicsOfInterest WHERE s.subscriber = :subscriber")
    List<Subscription> findBySubscriber(@Param("subscriber") String subscriber);

}
