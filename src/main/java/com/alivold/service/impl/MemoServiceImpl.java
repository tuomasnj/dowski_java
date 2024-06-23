package com.alivold.service.impl;

import com.alivold.dao.MemoMapper;
import com.alivold.domain.SysMemo;
import com.alivold.service.MemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemoServiceImpl implements MemoService {
    @Autowired
    private MemoMapper memoMapper;

    @Override
    public List<SysMemo> findUserMemos(Long userId, String month) {
        return memoMapper.selectMemosByUserId(userId, month);
    }
}
