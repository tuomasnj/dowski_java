package com.alivold.controller;
import cn.hutool.json.JSONObject;
import com.alivold.config.MinioConfig;
import com.alivold.domain.CommonFile;
import com.alivold.domain.PhotoImage;
import com.alivold.exception.BaseException;
import com.alivold.service.CommonFileService;
import com.alivold.util.LoginUserInfoUtil;
import com.alivold.util.MinioUtil;
import com.alivold.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/file")
@Slf4j
public class CommonFileController {
    @Autowired
    MinioConfig minioConfig;

    @Autowired
    MinioUtil minioUtil;

    @Autowired
    CommonFileService commonFileService;

    @Autowired
    LoginUserInfoUtil loginUserInfoUtil;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('sys:pic')")
    public ResponseResult uploadFile(@RequestParam("files") List<MultipartFile> files){
        Long userId = loginUserInfoUtil.getLoginUserId();
        try {
            //创建一组异步任务
            List<CompletableFuture<CommonFile>> futures = files.stream()
                    .map(file -> commonFileService.uploadFile(file, userId))
                    .collect(Collectors.toList());

            // 等待所有异步任务完成
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            //获取任务执行结果
            CompletableFuture<List<CommonFile>> tasks = allOf.thenApply(v ->
                    futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList())
            );
            List<CommonFile> res = tasks.get();
            return ResponseResult.success(res);
        } catch (Exception e) {
            throw new BaseException("服务异常");
        }
    }

    @PostMapping("/download")
    @PreAuthorize("hasAuthority('sys:pic')")
    public ResponseResult downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response){
        try {
            minioUtil.download(fileName, response);
        } catch (Exception e) {
            log.error("文件资源下载异常");
            throw new BaseException("文件下载错误！");
        }
        return ResponseResult.success();
    }

    @PostMapping("/ImgInfo")
    @PreAuthorize("hasAuthority('sys:pic')")
    public ResponseResult getImgInfo(){
        Long loginUserId = loginUserInfoUtil.getLoginUserId();
        List<PhotoImage> res = commonFileService.getImgInfo(loginUserId);
        return ResponseResult.success(res);
    }
}
