package com.alivold.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.alivold.config.MinioConfig;
import com.alivold.dao.ImgMapper;
import com.alivold.domain.CommonFile;
import com.alivold.domain.PhotoImage;
import com.alivold.exception.BaseException;
import com.alivold.service.CommonFileService;
import com.alivold.util.LoginUserInfoUtil;
import com.alivold.util.MinioUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class CommonFileServiceImpl implements CommonFileService {
    @Autowired
    MinioConfig minioConfig;

    @Autowired
    MinioUtil minioUtil;

    @Autowired
    ImgMapper imageMapper;

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<CommonFile> uploadFile(MultipartFile file, Long userId) {
        log.info("当前时间是：{}=====处理文件上传任务", DateUtil.format(new Date(), "yyyy/MM/dd HH:mm:ss"));
        try {
            String bucketName = minioConfig.getBucketName();
            CommonFile commonFile = new CommonFile();
            String originalFileName = file.getOriginalFilename();
            log.info("上传文件的名称为【{}】", originalFileName);
            String objectName = minioUtil.upload(file);
            if (objectName == null) {
                throw new BaseException("文件上传失败");
            }
            commonFile.setFileType("0");
            commonFile.setFileUrl(minioConfig.getEndpoint() + bucketName + '/' + objectName);
            String[] arr = commonFile.getFileUrl().split("/");
            commonFile.setFileName(arr[arr.length - 1]);
            //存储图片文件信息至数据库
            PhotoImage photoImage = new PhotoImage();
            photoImage.setImageName(commonFile.getFileName());
            photoImage.setImageUrl(commonFile.getFileUrl());
            photoImage.setCreatedTime(Calendar.getInstance().getTime());
            photoImage.setUserId(userId);
            int insert = imageMapper.insert(photoImage);
            return CompletableFuture.completedFuture(commonFile);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException("文件上传发生错误啦!!");
        }
    }

    @Override
    public List<PhotoImage> getImgInfo(Long loginUserId) {
        LambdaQueryWrapper<PhotoImage> photoImageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(loginUserId == null){
            return null;
        }else if(loginUserId == 1L  || loginUserId == 2L){
            photoImageLambdaQueryWrapper.in(PhotoImage::getUserId, Arrays.asList(new Long[]{1L, 2L}));
            List<PhotoImage> photoImages = imageMapper.selectList(photoImageLambdaQueryWrapper);
            return photoImages;
        }else{
            photoImageLambdaQueryWrapper.eq(PhotoImage::getUserId, loginUserId);
            List<PhotoImage> photoImages = imageMapper.selectList(photoImageLambdaQueryWrapper);
            return photoImages;
        }
    }
}
