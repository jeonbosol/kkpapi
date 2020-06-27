package com.pay.api.repository;

import com.pay.api.dto.Money;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.Optional;

@EnableMongoRepositories
public interface MoneyRepository extends MongoRepository<Money,String> {

    @Query(collation = "{'locale' : 'ko'}")
    Optional<Money> findByApiTokenAndStatusAndRoomIdAndUserId(String apiToken, String status, String roomId, String userId);

    @Query(collation = "{'locale' : 'ko'}")
    List<Money> findAllByApiTokenAndStatusAndRoomId(String apiToken, String status, String roomId);

}
