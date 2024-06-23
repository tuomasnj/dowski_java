package com.alivold.exception;

import lombok.Data;

@Data
public class BusinessException extends  RuntimeException{
    private Integer code = 201;

    public BusinessException(){
        super();
    }

    public BusinessException(String msg){
        super(msg);
    }

    public BusinessException(int code, String msg){
        super(msg);
        this.code = code;
    }
}
