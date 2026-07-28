package com.sookmyung.swapclass.global.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SuspendedUserException extends RuntimeException {

    private final LocalDateTime suspendedUntil;

    public SuspendedUserException(LocalDateTime suspendedUntil) {
        super("정지된 계정입니다.");
        this.suspendedUntil = suspendedUntil;
    }
}
