package com.alivold.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonFile {
    private Long id;

    private String fileName;

    private String fileUrl;

    //0图片 1文档
    private String fileType;
}
