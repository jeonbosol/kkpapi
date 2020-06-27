package com.pay.api.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResCode {
    성공("20",""),
    실패("00", ""),
    본인조회7일미만("30", "본인이 생성한 뿌리기가 아니거나 7일 이상 지난 조회입니다."),
    서버에러("40", " 서버 에러 입니다. 잠시후 다시 사용해 주시기 바랍니다."),
    필수파라미터부족("50", "필수파라미터가 없습니다."),
    토큰유효부족("60","유효하지 않은 토큰 입니다."),
    중복체크("70", "이미 받은 이력이 었습니다."),
    받기실패("80", "받기에 실패하였습니다."),
    본인토큰("90", "본인이 뿌린 돈은 받을 수 없습니다.");

    private String code;
    private String msg;
}
