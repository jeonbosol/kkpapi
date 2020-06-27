package com.pay.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document(collection = "apiToken", collation="ko")
@CompoundIndexes({
    @CompoundIndex(name="roomId_userId", def= "{'roomId':1, 'userId' :1}")
})
public class ApiToken {
    @Id
    @Field("_id")
    private String id;

    @Field("userId")
    private String userId;

    @Field("roomId")
    private String roomId;

    @Field("money")
    private int money;

    @Field("limitDts")
    private LocalDateTime limitDts;

    @Field("regDts")
    private LocalDateTime regDts;

    @Field("ttlDts")
    @Indexed(name="ttlDtsIndex", expireAfter = "7d")
    private LocalDateTime ttlDts;

}
