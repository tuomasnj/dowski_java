package com.alivold.service;

import com.alivold.domain.CommonFile;
import com.alivold.domain.PhotoImage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommonFileService {
    CompletableFuture<CommonFile> uploadFile(MultipartFile file, Long userId);

    Page<PhotoImage> getImgInfo(Long loginUserId, Integer current, Integer size);

    PhotoImage selectImg(String fileName);

    boolean deleteItem(String imageUrl, String fileName);
}
