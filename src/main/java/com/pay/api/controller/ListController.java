package com.pay.api.controller;

import com.pay.api.constant.MoneyStatus;
import com.pay.api.constant.ResCode;
import com.pay.api.data.ApiListResponse;
import com.pay.api.data.ApiResponse;
import com.pay.api.data.RequestData;
import com.pay.api.data.ResponseData;
import com.pay.api.dto.ApiToken;
import com.pay.api.dto.Money;
import com.pay.api.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ListController {
    @Autowired
    private ApiService apiService;

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @RequestMapping( value= "/api/list", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseData listApi(@RequestHeader("X-ROOM-ID")String roomId,
                                @RequestHeader("X-USER-ID")String userId,
                                @RequestBody RequestData requestData) {

        apiService.initRequestData(requestData, userId, roomId);
        if(apiService.validRequestData(requestData) == false) {
            return ResponseData.fail(ResCode.필수파라미터부족.getMsg(), ResCode.필수파라미터부족.getCode());
        }

        ApiResponse apiResponse  = new ApiResponse();

        try{
            Optional<ApiToken> apiToken = apiService.getTokenDataByUserId(requestData);
            apiToken.ifPresentOrElse(tk -> {
                ApiListResponse apiListResponse = new ApiListResponse();
                List<Money> moneyList = apiService.getMoneyList(requestData, MoneyStatus.받기완료.getStatus());
                int amount = moneyList.stream().map(m->m.getSpreadMoney()).reduce(0, Integer::sum);
                List<Map<Integer,String>> mList = moneyList.stream()
                        .map(m-> new HashMap<Integer, String>(){{
                            put(m.getSpreadMoney(), m.getUserId());
                        }}).collect(Collectors.toList());

                apiListResponse.setTokenDate(tk.getRegDts().format(dateTimeFormatter));
                apiListResponse.setSpreadMoney(tk.getMoney());
                apiListResponse.setReceiveMoney(amount);
                apiListResponse.setCompletedInfoList(mList);

                apiResponse.setCode(ResCode.성공.getCode());
                apiResponse.setData(apiListResponse);
                apiResponse.setMessage(ResCode.성공.getMsg());

            }, () -> {
                apiResponse.setCode(ResCode.본인조회7일미만.getCode());
                apiResponse.setMessage(ResCode.본인조회7일미만.getMsg());
            });
        } catch(Exception e) {
            apiResponse.setMessage(ResCode.서버에러.getMsg());
            apiResponse.setCode(ResCode.서버에러.getCode());
            return ResponseData.custom(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseData.custom(apiResponse, HttpStatus.OK);
    }
}
