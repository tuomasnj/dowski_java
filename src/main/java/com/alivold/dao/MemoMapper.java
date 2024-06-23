package com.alivold.dao;

import com.alivold.domain.SysMemo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemoMapper extends BaseMapper<SysMemo> {
     List<SysMemo> selectMemosByUserId(Long userId, String month);
}
