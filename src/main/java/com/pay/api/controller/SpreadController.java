package com.pay.api.controller;

import com.pay.api.constant.MoneyStatus;
import com.pay.api.constant.ResCode;
import com.pay.api.data.RequestData;
import com.pay.api.data.ResponseData;
import com.pay.api.dto.ApiToken;
import com.pay.api.dto.Money;
import com.pay.api.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class SpreadController {

    @Autowired
    private ApiService apiService;

    @RequestMapping( value= "/api/spread", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseData SpreadApi(@RequestHeader("X-ROOM-ID")String roomId,
                                  @RequestHeader("X-USER-ID")String userId,
                                  @RequestBody RequestData requestData) {
        apiService.initRequestData(requestData, userId, roomId);
        if(apiService.validRequestData(requestData) == false) {
            return ResponseData.fail(ResCode.필수파라미터부족.getMsg(), ResCode.필수파라미터부족.getCode());
        }

        String generatingRandomToken = apiService.makeToken(0);

        ApiToken cApiToken = new ApiToken();
        cApiToken.setId(generatingRandomToken);
        cApiToken.setMoney(requestData.getMoney());
        cApiToken.setRoomId(requestData.getRoomId());;
        cApiToken.setUserId(requestData.getUserId());
        cApiToken.setRegDts(LocalDateTime.now());
        cApiToken.setTtlDts(LocalDateTime.now());
        cApiToken.setLimitDts(LocalDateTime.now().plusMinutes(10));

        List<Integer> spreadMoneyList = apiService.SpreadMoney(requestData.getMoney(), requestData.getMemberNum());
        List<Money> moneyList = new ArrayList<>();

        for(Integer spreadMoney : spreadMoneyList) {
            Money money = new Money();
            money.setApiToken(generatingRandomToken);
            money.setRoomId(requestData.getRoomId());
            money.setUserId ("");
            money.setSpreadMoney(spreadMoney);
            money.setStatus(MoneyStatus.받기미완료.getStatus());
            money.setTtlDts(LocalDateTime.now());
            moneyList.add(money);
        }

        apiService.saveToken(cApiToken);
        apiService.saveMoneyList(moneyList);

        return ResponseData.success(generatingRandomToken);
    }


}
