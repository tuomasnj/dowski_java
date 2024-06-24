package com.alivold.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alivold.dao.MemoMapper;
import com.alivold.domain.SysMemo;
import com.alivold.exception.BusinessException;
import com.alivold.service.MemoService;
import com.alivold.util.LoginUserInfoUtil;
import com.alivold.util.ResponseResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/memo")
@Slf4j
public class MemoController {
    @Autowired
    private LoginUserInfoUtil loginUserInfoUtil;

    @Autowired
    private MemoMapper memoMapper;

    @Autowired
    private MemoService memoService;
    @PostMapping("/lists")
    public ResponseResult getUserMeomos(@RequestBody JSONObject jsonObject){
        Long loginUserId = loginUserInfoUtil.getLoginUserId();
        String month = jsonObject.getString("month");
        log.info("id为【{}】的用户查询了【{}】月的备忘录");
        List<SysMemo> userMemos = memoService.findUserMemos(loginUserId, month);
        return ResponseResult.success(userMemos);
    }

    @PostMapping("/addnew")
    public ResponseResult addMemoItem(@RequestBody SysMemo memo){
        Long userId = loginUserInfoUtil.getLoginUserId();
        memo.setUserId(userId);
        if(memo.getNotifyTime() == null || memo.getNotifyTime().equals("")){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(memo.getMemoDate());
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            memo.setNotifyTime(calendar.getTime());
        }
        try {
            int insert = memoMapper.insert(memo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("添加备忘录事件异常");
        }
        return ResponseResult.success();
    }

    @PostMapping("/edit")
    public ResponseResult updateMemoItem(@RequestBody SysMemo memo){
        Long userId = loginUserInfoUtil.getLoginUserId();
        memo.setUserId(userId);
        if(memo.getNotifyTime() == null || memo.getNotifyTime().equals("")){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(memo.getMemoDate());
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            memo.setNotifyTime(calendar.getTime());
        }
        LambdaQueryWrapper<SysMemo> sysMemoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysMemoLambdaQueryWrapper.eq(SysMemo::getUserId, userId);
        sysMemoLambdaQueryWrapper.eq(SysMemo::getMemoDate, memo.getMemoDate());
        int update;
        try {
            update = memoMapper.update(memo, sysMemoLambdaQueryWrapper);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("修改备忘录事件异常");
        }
        return update > 0 ? ResponseResult.success(): ResponseResult.fail();
    }

    @PostMapping("/delete")
    public ResponseResult deleteMemoItem(@RequestBody SysMemo memo){
        Long userId = loginUserInfoUtil.getLoginUserId();
        Date date = memo.getMemoDate();
        LambdaQueryWrapper<SysMemo> sysMemoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysMemoLambdaQueryWrapper.eq(SysMemo::getUserId, userId);
        sysMemoLambdaQueryWrapper.eq(SysMemo::getMemoDate, date);
        int delete = 0;
        try {
            delete = memoMapper.delete(sysMemoLambdaQueryWrapper);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException("删除异常");
        }
        return delete > 0 ? ResponseResult.success(): ResponseResult.fail();
    }
}
