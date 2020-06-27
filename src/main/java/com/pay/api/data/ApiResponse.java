package com.pay.api.data;

import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {

    private String message;
    private String code;
    private Object data;

    public ApiResponse() {}
}
