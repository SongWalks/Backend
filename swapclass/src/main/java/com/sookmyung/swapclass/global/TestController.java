package com.sookmyung.swapclass.global;

import com.sookmyung.swapclass.global.exception.CustomException;
import com.sookmyung.swapclass.global.exception.ErrorCode;
import com.sookmyung.swapclass.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test/success")
    public ApiResponse<String> testSuccess() {
        return ApiResponse.success("정상 응답입니다.");
    }

    @GetMapping("/api/test/error")
    public void testError() {
        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }
}