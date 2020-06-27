package com.pay.api.service;

import com.pay.api.data.RequestData;

import java.time.LocalDateTime;
import java.util.Random;

public interface ApiServiceBase {

    default void initRequestData(RequestData requestData, String userId, String roomId) {
        requestData.setUserId(userId);
        requestData.setRoomId(roomId);
    }

    default boolean validRequestData(RequestData requestData) {
        if(requestData.getRoomId().isBlank() || requestData.getUserId().isBlank()){
            return false;
        }
        return true;
    }

    default String GeneratingRandomToken() {
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

    /**
     * 뿌리기 등록한 시간보다 10분이상인 경우 false
     * @param limitDts
     * @return
     */
    default boolean checkTime(LocalDateTime limitDts) {
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
    default boolean checkUser(String regpeUser, String currentUser) {
        if(regpeUser.equals(currentUser)) {
            return false;
        }
        return true;
    }
}
