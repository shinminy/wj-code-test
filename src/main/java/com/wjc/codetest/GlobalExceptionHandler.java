package com.wjc.codetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice(value = {"com.wjc.codetest.product.controller"})
public class GlobalExceptionHandler {

    /*
     * 1. 문제: 에러 처리 (오류 응답 규격의 일관성 부족 및 비즈니스 예외 구분 부재)
     * 2. 원인: 코드 및 설계 (RuntimeException 중심의 포괄 처리로 인해 HTTP 상태 코드와 오류 의미가 명확히 표현되지 않음)
     * 3. 개선안:
     *    - ProductNotFoundException 처리 핸들러 추가
     *    - 모든 핸들러가 ResponseEntity<ErrorResponse>를 반환하도록 통일하여 오류 응답 구조 표준화
     */

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    /*
     * 1. 문제: 가독성 (메소드명이 역할 설명에 부적합)
     * 2. 원인: 코드 (runTimeException이라는 이름이 핸들러 역할을 명확하게 표현하지 못함)
     * 3. 개선안: handleRuntimeException으로 메소드명을 변경하여 해당 메소드가 런타임 예외를 '처리(handle)'하는 핸들러임을 명시하여 가독성 향상
     */
    public ResponseEntity<String> runTimeException(Exception e) {
        log.error("status :: {}, errorType :: {}, errorCause :: {}",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "runtimeException",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
