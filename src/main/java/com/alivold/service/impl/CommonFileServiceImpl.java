package com.alivold.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alivold.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.alivold.config.MinioConfig;
import com.alivold.dao.ImgMapper;
import com.alivold.domain.CommonFile;
import com.alivold.domain.PhotoImage;
import com.alivold.exception.BaseException;
import com.alivold.service.CommonFileService;
import com.alivold.util.MinioUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CommonFileServiceImpl extends ServiceImpl<ImgMapper, PhotoImage> implements CommonFileService {
    @Autowired
    MinioConfig minioConfig;

    @Autowired
    MinioUtil minioUtil;

    @Autowired
    ImgMapper imageMapper;

    @Value("${minio.bucketName}")
    String bucketName;

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
    public Page<PhotoImage> getImgInfo(Long loginUserId, Integer current, Integer size) {
        LambdaQueryWrapper<PhotoImage> photoImageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        photoImageLambdaQueryWrapper.orderByDesc(PhotoImage::getCreatedTime);
        Page<PhotoImage> page = new Page<>(current, size);
        if(loginUserId == null){
            return null;
        }else if(loginUserId == 1L  || loginUserId == 2L){
            photoImageLambdaQueryWrapper.in(PhotoImage::getUserId, Arrays.asList(new Long[]{1L, 2L}));
            Page<PhotoImage> photoImages = this.page(page, photoImageLambdaQueryWrapper);
            return photoImages;
        }else{
            photoImageLambdaQueryWrapper.eq(PhotoImage::getUserId, loginUserId);
            Page<PhotoImage> photoImages = this.page(page, photoImageLambdaQueryWrapper);
            return photoImages;
        }
    }

    @Override
    public PhotoImage selectImg(String fileName) {
        LambdaQueryWrapper<PhotoImage> photoImageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        photoImageLambdaQueryWrapper.eq(PhotoImage::getImageName, fileName);
        PhotoImage image = imageMapper.selectOne(photoImageLambdaQueryWrapper);
        return image;
    }

    @Override
    @Transactional
    public boolean deleteItem(String imageUrl, String fileName) {
        try {
            //根据fileName删除数据库记录
            LambdaQueryWrapper<PhotoImage> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PhotoImage::getImageName, fileName);
            int delete = imageMapper.delete(queryWrapper);
            if(delete == 0){
                throw new BaseException("图片删除失败");
            }
            //根据url和bucketName找到remove方法的文件名参数
            String str = bucketName + "/";
            int idx = imageUrl.indexOf(str);
            String target = imageUrl.substring(idx + str.length());
            //2024-11/03/8b735ea834004bf8b4b4fa816a27352a.jpg
            log.info("文件名：【{}】",target);
            boolean remove = minioUtil.remove(target);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("服务异常");
        }
    }
}
