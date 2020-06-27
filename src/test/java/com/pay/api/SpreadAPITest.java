package com.pay.api;

import com.pay.api.config.MongoDBConfiguration;
import com.pay.api.constant.MoneyStatus;
import com.pay.api.dto.Money;
import com.pay.api.dto.ApiToken;
import com.pay.api.repository.MoneyRepository;
import com.pay.api.repository.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@DataMongoTest()
@Import(MongoDBConfiguration.class)
public class SpreadAPITest {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    MoneyRepository moneyRepository;

    @Test
    public void 토큰정보가져오기테스트() {
        String test = "EZG";
        Optional<ApiToken> token = tokenRepository.findById(test);
        System.out.println("token : " + token.get());

        assert token != null : "Token is null";
    }

    @Test
    public void 토큰생성테스트() {
        String generatingRandomToken = makeToken(0);
        assert generatingRandomToken != null : "Fail makeToekn";
    }

    @Test
    public void 금액분배테스트() {
        int money = 755;
        int memberNum = 2;
        List<Integer> spreadMoneyList = SpreadMoney(money, memberNum);
        int total = spreadMoneyList.stream().reduce(0, Integer::sum);

        System.out.println("money : " + money + " / total : " + total);
        assert money == total : "Fail SpreadMoney";
    }

    @Test
    public void 토큰금액저장테스트() {
        String generatingRandomToken = makeToken(0);
        String userId = "testUser1";
        String roomId = "test1";
        int paramMoney = 800;
        int memberNum = 5;

        ApiToken cApiToken = new ApiToken();
        cApiToken.setId(generatingRandomToken);
        cApiToken.setMoney(751);
        cApiToken.setRoomId(roomId);;
        cApiToken.setUserId(userId);
        cApiToken.setRegDts(LocalDateTime.now());
        cApiToken.setTtlDts(LocalDateTime.now());
        cApiToken.setLimitDts(LocalDateTime.now().plusMinutes(10));


        List<Integer> spreadMoneyList = SpreadMoney(paramMoney, memberNum);
        List<Money> moneyList = new ArrayList<>();

        for(Integer spreadMoney : spreadMoneyList) {
            Money money = new Money();
            money.setApiToken(generatingRandomToken);
            money.setRoomId(roomId);
            money.setUserId ("");
            money.setSpreadMoney(spreadMoney);
            money.setStatus(MoneyStatus.받기미완료.getStatus());
            money.setTtlDts(LocalDateTime.now());
            moneyList.add(money);
        }

        saveToken(cApiToken);
        saveMoneyList(moneyList);
    }

    public List<Integer> SpreadMoney(int money, int memberNum) {
        List<Integer> spreadMoneyList = new ArrayList<>();
        int prevMoney = 0;
        Random random = new Random();

        for(var i = 0; i < (memberNum-1); i++) {
            int randomMoney = random.ints(1, (money - prevMoney)).findFirst().getAsInt();
            prevMoney += randomMoney;
            spreadMoneyList.add(randomMoney);
        }

        int lastMoney = money - prevMoney;
        spreadMoneyList.add(lastMoney);

        return spreadMoneyList;
    }

    @Transactional
    public void saveToken(ApiToken cApiToken) {
        tokenRepository.insert(cApiToken);
    }

    @Transactional
    public void saveMoneyList(List<Money> moneyList) {
        moneyRepository.saveAll(moneyList);
    }

    public String makeToken(int r) {
        //token 생성 10번 이상 요청시 실패로 처리
        if(r > 10) {
            return null;
        }
        String generatingRandomToken = GeneratingRandomToken();
        Optional<ApiToken> token = tokenRepository.findById(generatingRandomToken);

        if(token.isEmpty()) {
            System.out.println("token create " + generatingRandomToken);
            return generatingRandomToken;
        } else {
            return makeToken(r+1);
        }
    }

    public String GeneratingRandomToken() {
        int leftLimit = 65;
        int rightLimit = 122;
        int targetStringLength = 3;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(x -> !(x >= 91 && x <=96))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }
}
