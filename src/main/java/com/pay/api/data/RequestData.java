package com.pay.api.data;

import lombok.Data;

@Data
public class RequestData {
    private String apiToken;
    private String userId;
    private String roomId;
    private int money;
    private int memberNum;

}
