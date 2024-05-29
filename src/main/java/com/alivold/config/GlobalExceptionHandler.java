package com.alivold.config;

import com.alivold.exception.BaseException;
import com.alivold.exception.BusinessException;
import com.alivold.util.ResponseResult;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE) // 确保是最高优先级的异常处理器
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseResult handleBaseException(BaseException e) {
        return ResponseResult.fail(e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseResult handleBusinessException(BusinessException e) {
        return ResponseResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseResult handleFileUploadException(FileUploadException e){
        if(e instanceof SizeException){
            return ResponseResult.fail("文件大小超限制");
        }else {
            e.printStackTrace();
            return ResponseResult.fail("文件上传异常");
        }
    }
}

