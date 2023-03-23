package com.back2261.applicationservice.interfaces.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TransactionCode {
    DEFAULT_100(100, "Success", HttpStatus.OK),
    EMAIL_EXISTS(101, "Email already exists", HttpStatus.OK),
    EMAIL_SEND_FAILED(102, "Email send error", HttpStatus.OK),
    USER_NOT_FOUND(103, "User not found", HttpStatus.OK),
    USER_ALREADY_VERIFIED(104, "User already verified", HttpStatus.OK),
    VERIFICATION_CODE_NOT_FOUND(105, "Entered code is invalid", HttpStatus.OK),
    USER_NOT_VERIFIED(106, "User not verified", HttpStatus.OK),
    USERNAME_EXISTS(107, "Username already taken", HttpStatus.OK),
    WRONG_PASSWORD(108, "Entered password is wrong", HttpStatus.OK),
    USER_NOT_COMPLETED(109, "User details not finished", HttpStatus.OK),
    TOKEN_INVALID(110, "Token is invalid", HttpStatus.OK),
    TOKEN_NOT_FOUND(111, "Token not found", HttpStatus.OK),
    PASSWORD_SAME(112, "New password cannot be same as old password", HttpStatus.OK),
    USER_BLOCKED(113, "User is blocked", HttpStatus.OK),
    DB_ERROR(-99, "Database Error", HttpStatus.OK);

    private final int id;
    private final String code;
    private final HttpStatus httpStatus;

    TransactionCode(int id, String code, HttpStatus httpStatus) {
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
