package com.pay.api;

import com.pay.api.dto.Token;
import com.pay.api.repo.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@DataMongoTest
public class SpreadTest {

    @Autowired
    TokenRepository tokenRepository;

    @Test
    public void 토큰정보가져오기테스트() {
        String test = "EZG";
        Optional<Token> token = tokenRepository.findById(test);
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
        int money = 751;
        int memberNum = 5;
        List<Integer> spreadMoneyList = SpreadMoney(money, memberNum);
        int total = spreadMoneyList.stream().reduce(0, Integer::sum);

        System.out.println("money : " + money + " / total : " + total);
        assert money == total : "Fail SpreadMoney";
    }

    @Test
    public void 토큰금액저장테스트() {
        String generatingRandomToken = makeToken(0);

        Token cToken = new Token();
        cToken.id = generatingRandomToken;
        cToken.roomId = "test1";
        cToken.userId = "testUser1";
        cToken.regDts = LocalDateTime.now();
        cToken.limitDts = LocalDateTime.now().plusMinutes(10);

        saveToken(cToken);


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
    public void saveToken(Token cToken) {
        tokenRepository.insert(cToken);
    }

    public String makeToken(int r) {
        //token 생성 10번 이상 요청시 실패로 처리
        if(r > 10) {
            return null;
        }
        String generatingRandomToken = GeneratingRandomToken();
        Optional<Token> token = tokenRepository.findById(generatingRandomToken);

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
