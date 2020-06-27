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
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
@Import(MongoDBConfiguration.class)
public class ReceiveAPITest {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    MoneyRepository moneyRepository;

    RequestData requestData;

    @BeforeEach
    public void DATA_INIT() {
        requestData = new RequestData();
        requestData.setApiToken("cCW");
        requestData.setRoomId("test1");
        requestData.setUserId("testUser6");
    }

    @Test
    public void 토큰매핑_유효성_테스트() {
        boolean check = checkApiToken(requestData);
        assert check == true : "Fail check";
    }

    @Test
    public void 머니_유저_유효성_테스트() {
        boolean chkUser = checkUserMoney(requestData);
        assert chkUser == true : "Fail chkUser";
    }

    @Test
    public void 머니_받고_저장_테스트() {
        AtomicBoolean chk = new AtomicBoolean(true);

        Optional<Money> money = getMoney(requestData, MoneyStatus.받기미완료.getStatus());
        money.ifPresentOrElse(m -> {
            m.setStatus(MoneyStatus.받기완료.getStatus());
            m.setUserId(requestData.getUserId());
            m.setRegDts(LocalDateTime.now());
            saveMoney(m);
        }, () -> {
            chk.set(false);
        });
        assert chk.get() == true : "Fail MoneyGetAndSave";
    }

    public boolean checkApiToken(RequestData rd) {
        AtomicBoolean chk = new AtomicBoolean(false);
        Optional<ApiToken> token = tokenRepository.findByIdAndRoomId(rd.getApiToken(), rd.getRoomId());
        token.ifPresent(tk -> {
            if (checkTime(tk.getLimitDts()) && checkUser(tk.getUserId(), rd.getUserId())) {
                chk.set(true);
            }
        });
        return chk.get();
    }

    /**
     * 뿌리기 등록한 시간보다 10분이상인 경우 false
     * @param limitDts
     * @return
     */
    public boolean checkTime(LocalDateTime limitDts) {
        if(limitDts.compareTo(LocalDateTime.now()) > 0) {
            return true;
        }
        return false;
    }


    /**
     * 뿌리기 등록한 유저인경우 false
     * @param regpeUser
     * @param currentUser
     * @return
     */
    public boolean checkUser(String regpeUser, String currentUser) {
        if(regpeUser.equals(currentUser)) {
            return false;
        }
        return true;
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

    public Optional<Money> getMoney(RequestData rd, String status) {
        List<Money> moneyList = moneyRepository
                .findAllByApiTokenAndStatusAndRoomId(rd.getApiToken(),
                        status,
                        rd.getRoomId());
        return moneyList.stream().findFirst();
    }

    @Transactional
    public void saveMoney(Money m) {
        moneyRepository.save(m);
    }
}
