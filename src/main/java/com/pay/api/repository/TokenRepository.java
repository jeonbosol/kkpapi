package com.pay.api.repository;

import com.pay.api.dto.ApiToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Optional;

@EnableMongoRepositories
public interface TokenRepository extends MongoRepository<ApiToken, String> {
    Optional<ApiToken> findById(String token);

    @Query(collation = "{'locale' : 'ko'}")
    Optional<ApiToken> findByIdAndRoomId(String id, String roomId);

    @Query(collation = "{'locale' : 'ko'}")
    Optional<ApiToken> findByIdAndRoomIdAndUserId(String id, String roomId, String userId);

}
