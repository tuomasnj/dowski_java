package com.alivold.task;

import cn.hutool.core.date.DateUtil;
import com.alivold.dao.MemoMapper;
import com.alivold.domain.SysMemo;
import com.alivold.service.EmailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    /**
     * 发送备忘录通知到邮箱
     */
    @Scheduled(cron = "0 * * * * ?")
    public void sendNotice() {
        // String time = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
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
            //发送邮件通知
           // emailService.sendRemindEmail1();
        }
    }
}
