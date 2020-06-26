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
@Document(collation = "token")
public class Token {
    @Id
    public String id;

    public String userId;

    public String roomId;

    public int money;

    public LocalDateTime limitDts;

    public LocalDateTime regDts;

}
