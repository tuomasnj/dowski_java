package com.alivold.service;

import com.alivold.domain.SysMemo;

import java.util.List;

public interface MemoService {
    List<SysMemo> findUserMemos(Long userId, String month);
}
