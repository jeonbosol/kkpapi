package com.pay.api;

import com.pay.api.config.MongoDBConfiguration;
import com.pay.api.constant.MoneyStatus;
import com.pay.api.data.RequestData;
import com.pay.api.dto.ApiToken;
import com.pay.api.dto.Money;
import com.pay.api.repository.MoneyRepository;
import com.pay.api.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@DataMongoTest()
@Import(MongoDBConfiguration.class)
public class ListAPITest {
    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    MoneyRepository moneyRepository;

    RequestData requestData;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    public void DATA_INIT() {
        requestData = new RequestData();
        requestData.setApiToken("cCW");
        requestData.setRoomId("test1");
        requestData.setUserId("testUser1");
    }

    @Test
    public void 토큰_데이터_가져오기_TEST() {
        AtomicBoolean chk = new AtomicBoolean(true);

        Optional<ApiToken> apiToken = getTokenDataByUserId(requestData);
        apiToken.ifPresentOrElse(tk -> {
            List<Money> moneyList = getMoneyList(requestData, MoneyStatus.받기완료.getStatus());
            int amount = moneyList.stream().map(m->m.getSpreadMoney()).reduce(0, Integer::sum);
            List<Map<Integer,String>> mList = moneyList.stream()
                                                .map(m-> new HashMap<Integer, String>(){{
                                                    put(m.getSpreadMoney(), m.getUserId());
                                                }}).collect(Collectors.toList());

            System.out.println( " 뿌린시각 : " +  tk.getRegDts().format(dateTimeFormatter)
                    + "\n 뿌린금액 : " + tk.getMoney()
                    + "\n 받은금액 : " + amount
                    + "\n 받기완료된 정보" + mList);
        }, () -> {
            chk.set(false);
        });

        assert chk.get() == true : "Fail TokenDataList";
    }

    public List<Money> getMoneyList(RequestData rd, String status) {
        return moneyRepository
                .findAllByApiTokenAndStatusAndRoomId(rd.getApiToken(),
                        status,
                        rd.getRoomId());
    }

    public Optional<ApiToken> getTokenDataByUserId(RequestData rd) {
        return tokenRepository.findByIdAndRoomIdAndUserId(rd.getApiToken(),rd.getRoomId(), rd.getUserId());
    }

}
