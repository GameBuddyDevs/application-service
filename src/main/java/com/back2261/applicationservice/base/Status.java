package com.back2261.applicationservice.base;

import com.back2261.applicationservice.interfaces.enums.TransactionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Status {

    private String code;
    private Boolean success;
    private String message;

    public Status(TransactionCode transactionCode) {
        this.code = String.valueOf(transactionCode.getId());
        this.success = transactionCode.getHttpStatus().is2xxSuccessful();
        this.message = transactionCode.getCode();
    }
}
