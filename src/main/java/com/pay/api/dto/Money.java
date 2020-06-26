package com.pay.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document(collation = "money")
public class Money {
    @Id
    public String id;

    public String token;

    public String userId;

    public String roomId;

    public String status;

    public int spreadMoney;

    public LocalDateTime regDts;
}
