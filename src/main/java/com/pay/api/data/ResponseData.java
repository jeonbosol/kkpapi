package com.pay.api.data;

import com.pay.api.constant.ResCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseData extends ResponseEntity<ApiResponse> {

    public ResponseData(ApiResponse body, HttpStatus status) {
        super(body, status);
    }

    public static ResponseData success(Object data) {
        return new ResponseData(ApiResponse.builder().message(ResCode.성공.getMsg()).code(ResCode.성공.getCode()).data(data).build(), HttpStatus.OK);
    }

    public static ResponseData fail(String msg, String code) {
        return new ResponseData(ApiResponse.builder().message(msg).code(code).build(), HttpStatus.OK);
    }

    public static ResponseData custom(ApiResponse apiResponse, HttpStatus httpStatus) {
        return new ResponseData(apiResponse, httpStatus);
    }
}
