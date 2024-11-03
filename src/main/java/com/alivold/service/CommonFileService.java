package com.alivold.service;

import com.alivold.domain.CommonFile;
import com.alivold.domain.PhotoImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommonFileService {
    CompletableFuture<CommonFile> uploadFile(MultipartFile file, Long userId);

    List<PhotoImage> getImgInfo(Long loginUserId);
}
