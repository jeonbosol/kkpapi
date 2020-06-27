package com.pay.api.controller;

import com.pay.api.constant.MoneyStatus;
import com.pay.api.constant.ResCode;
import com.pay.api.data.RequestData;
import com.pay.api.data.ResponseData;
import com.pay.api.dto.Money;
import com.pay.api.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class ReceiveController {

    @Autowired
    private ApiService apiService;

    @RequestMapping( value= "/api/receive", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseData ReceiveApi(@RequestHeader("X-ROOM-ID")String roomId,
                                  @RequestHeader("X-USER-ID")String userId,
                                  @RequestBody RequestData requestData) {

        apiService.initRequestData(requestData, userId, roomId);
        if(apiService.validRequestData(requestData) == false) {
            return ResponseData.fail(ResCode.필수파라미터부족.getMsg(), ResCode.필수파라미터부족.getCode());
        }

        ResCode apiTokenChk = apiService.checkApiToken(requestData);
        if(apiTokenChk != null) {
            return ResponseData.fail(apiTokenChk.getMsg(), apiTokenChk.getCode());
        }

        boolean chkUserMoney = apiService.checkUserMoney(requestData);
        if(chkUserMoney == false) {
            return ResponseData.fail(ResCode.중복체크.getMsg(), ResCode.중복체크.getCode());
        }

        AtomicBoolean receivechk = new AtomicBoolean(true);

        Optional<Money> money = apiService.getMoney(requestData, MoneyStatus.받기미완료.getStatus());
        money.ifPresentOrElse(m -> {
            m.setStatus(MoneyStatus.받기완료.getStatus());
            m.setUserId(requestData.getUserId());
            m.setRegDts(LocalDateTime.now());
            apiService.saveMoney(m);
        }, () -> {
            receivechk.set(false);
        });

        if(receivechk.get() == false) {
            return ResponseData.fail(ResCode.중복체크.getMsg(), ResCode.중복체크.getCode());
        }

        return ResponseData.success("받은금액 " + money.get().getSpreadMoney());
        

    }
}
