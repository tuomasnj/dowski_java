package com.alivold;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alivold.config.MinioConfig;
import com.alivold.util.MinioUtil;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TestClass {
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioUtil minioUtil;

    @Test
    public void test1(){
        log.info("今天是【{}】", "周二");
        System.out.println(minioClient);
        System.out.println(StrUtil.isBlank("   "));
        Boolean exist = minioUtil.bucketExists(minioConfig.getBucketName());
        log.info("bucketName为dowski是否存在,{}", exist);
        System.out.println(minioUtil.makeBucket("xiaodowski"));
    }

    @Test
    public void testDeleteMinio(){
        System.out.println(minioUtil.removeBucket("xiaodowski"));
    }

    @Test
    public void testGetAllBuckets(){
        for(Bucket b: minioUtil.getAllBuckets()){
            log.info("Bucket名称为==={}", b.name());
        }
    }

    @Test
    public void testUtil(){
        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
        System.out.println(DateUtil.format(new Date(), "yyyy-MM/dd"));
    }

    @Test
    public void testFileDelete(){
        boolean ans = minioUtil.remove("2024-05/29/e82bdf2e0c734d9699b9d045a42305b7.pptx");
        if(ans){
            System.out.println("文件删除成功");
        }
    }

    @Test
    public void testImgPreview(){
        //获取图片访问地址需要使用全文件名换取url
        String previewUrl = minioUtil.preview("2024-05/29/f19a1a04c61742e790ab302e56d5b9f0.jpg");
        log.info("图片预览地址{}", previewUrl);
    }

    @Test
    public void testBucketInfo(){
        List<Item> items = minioUtil.listObjects();
        System.out.println(items);
    }
}
