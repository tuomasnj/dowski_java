package com.alivold.controller;

import cn.hutool.json.JSONObject;
import com.alivold.config.MinioConfig;
import com.alivold.exception.BaseException;
import com.alivold.util.MinioUtil;
import com.alivold.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
@Slf4j
public class CommonFileController {
    @Autowired
    MinioConfig minioConfig;

    @Autowired
    MinioUtil minioUtil;

    @PostMapping("/upload")
    public ResponseResult uploadFile(@RequestParam("files") List<MultipartFile> files){
        String bucketName = minioConfig.getBucketName();
        List<String> urls = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        for(MultipartFile file: files){
            String originalFileName =file.getOriginalFilename();
            log.info("上传文件的名称为【{}】", originalFileName);
            String objectName = minioUtil.upload(file);
            if(objectName == null){
                throw new BaseException("文件上传失败");
            }
            urls.add(minioConfig.getEndpoint() + bucketName + '/' + objectName);
        }
        // TODO: 2024/5/29 存储文件信息至数据库
        jsonObject.set("urls", urls);
        return ResponseResult.success(jsonObject);
    }

    @PostMapping("/download")
    public ResponseResult downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response){
        try {
            minioUtil.download(fileName, response);
        } catch (Exception e) {
            log.error("文件资源下载异常");
            throw new BaseException("文件下载错误！");
        }
        return ResponseResult.success();
    }
}
