package com.alivold.task;

import cn.hutool.core.date.DateUtil;
import com.alivold.dao.MemoMapper;
import com.alivold.dao.SysUserMapper;
import com.alivold.domain.SysMemo;
import com.alivold.domain.SysUser;
import com.alivold.exception.BusinessException;
import com.alivold.service.EmailService;
import com.alivold.util.LoginUserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ScheduledTask {
    @Autowired
    private MemoMapper memoMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private LoginUserInfoUtil loginUserInfoUtil;

    /**
     * 发送备忘录通知到邮箱
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void sendNotice() {
        // String time =  HH:mm:ss");
        //  log.info("现在是北京时间【{}】", time);
        LambdaQueryWrapper<SysMemo> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(SysMemo::getNotifyTime, new Date());
        wrapper.eq(SysMemo::getStatus, 0);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        wrapper.eq(SysMemo::getMemoDate, today.getTime());
        List<SysMemo> sysMemos = memoMapper.selectList(wrapper);
        for(SysMemo s : sysMemos){
            s.setStatus(1);
            try {
                LambdaQueryWrapper<SysMemo> memoWrapper = new LambdaQueryWrapper<>();
                memoWrapper.eq(SysMemo::getUserId, s.getUserId());
                memoWrapper.eq(SysMemo::getMemoDate, today.getTime());
                memoMapper.update(s, memoWrapper);
                //查事件对应的用户
                SysUser sysUser = sysUserMapper.selectById(s.getUserId());
                //发送邮件通知
                emailService.sendRemindEmail1(sysUser.getEmail(), DateUtil.format(new Date(), "yyyy年M月d日") + "备忘事项提醒",
                        DateUtil.format(new Date(), "yyyy年M月d日") + "待办", s.getEventContent());
                log.info("通知了内容为【{}】的备忘事项",s.getEventContent());
            } catch (Exception e) {
                log.error("【{}】",e.getMessage());
                throw new BusinessException("发送邮件通知异常");
            }
        }
    }
}
