package com.pay.api.data;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ApiListResponse {
    private String tokenDate;
    private int spreadMoney;
    private int receiveMoney;
    private List<Map<Integer,String>> completedInfoList;

}
