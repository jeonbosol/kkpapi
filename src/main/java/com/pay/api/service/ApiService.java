package com.pay.api.service;

import com.pay.api.constant.MoneyStatus;
import com.pay.api.constant.ResCode;
import com.pay.api.data.RequestData;
import com.pay.api.dto.ApiToken;
import com.pay.api.dto.Money;
import com.pay.api.repository.MoneyRepository;
import com.pay.api.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class ApiService implements ApiServiceBase {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private MoneyRepository moneyRepository;

    private ResCode chkToken;

    public List<Integer> SpreadMoney(int money, int memberNum) {
        List<Integer> spreadMoneyList = new ArrayList<>();
        int prevMoney = 0;
        Random random = new Random();

        for(var i = 0; i < (memberNum-1); i++) {
            if( (money - prevMoney) < 1) {
                break;
            }
            int randomMoney = random.ints(1, (money - prevMoney)).findFirst().getAsInt();
            prevMoney += randomMoney;
            spreadMoneyList.add(randomMoney);
        }

        int lastMoney = money - prevMoney;
        spreadMoneyList.add(lastMoney);

        return spreadMoneyList;
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


    public ResCode checkApiToken(RequestData rd) {
        chkToken = null;
        Optional<ApiToken> token = tokenRepository.findByIdAndRoomId(rd.getApiToken(), rd.getRoomId());
        token.ifPresent(tk -> {
            if (checkTime(tk.getLimitDts()) == false) {
                chkToken = ResCode.토큰유효부족;
            } else if(checkUser(tk.getUserId(), rd.getUserId()) == false) {
                chkToken = ResCode.본인토큰;
            }
        });
        return chkToken;
    }

    /**
     * 뿌리기 받은 내역이 없으면 true
     * @param rd
     * @return
     */
    public boolean checkUserMoney(RequestData rd) {
        Optional<Money> userMoney = moneyRepository
                .findByApiTokenAndStatusAndRoomIdAndUserId(rd.getApiToken()
                        , MoneyStatus.받기완료.getStatus()
                        , rd.getRoomId()
                        , rd.getUserId());
        if(userMoney.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW, timeout=3000)
    public void saveToken(ApiToken cApiToken) {
        tokenRepository.insert(cApiToken);
    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW, timeout=3000)
    public void saveMoney(Money m) {
        moneyRepository.save(m);
    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW, timeout=3000)
    public void saveMoneyList(List<Money> moneyList) {
        moneyRepository.saveAll(moneyList);
    }

    public Optional<Money> getMoney(RequestData rd, String status) {
        List<Money> moneyList = moneyRepository
                .findAllByApiTokenAndStatusAndRoomId(rd.getApiToken(), status, rd.getRoomId());
        return moneyList.stream().findFirst();
    }

    public List<Money> getMoneyList(RequestData rd, String status) {
        return moneyRepository
                .findAllByApiTokenAndStatusAndRoomId(rd.getApiToken(), status, rd.getRoomId());
    }

    public Optional<ApiToken> getTokenDataByUserId(RequestData rd) {
        return tokenRepository.findByIdAndRoomIdAndUserId(rd.getApiToken(),rd.getRoomId(), rd.getUserId());
    }
}
