package com.alivold;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alivold.config.MinioConfig;
import com.alivold.dao.MemoMapper;
import com.alivold.domain.SysMemo;
import com.alivold.service.EmailService;
import com.alivold.util.LoginUserInfoUtil;
import com.alivold.util.MinioUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TestClass {
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MemoMapper memoMapper;

    @Autowired
    private LoginUserInfoUtil loginUserInfoUtil;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void test1() {
        log.info("今天是【{}】", "周二");
        System.out.println(minioClient);
        System.out.println(StrUtil.isBlank("   "));
        Boolean exist = minioUtil.bucketExists(minioConfig.getBucketName());
        log.info("bucketName为dowski是否存在,{}", exist);
        System.out.println(minioUtil.makeBucket("xiaodowski"));
    }

    @Test
    public void testDeleteMinio() {
        System.out.println(minioUtil.removeBucket("xiaodowski"));
    }

    @Test
    public void testGetAllBuckets() {
        for (Bucket b : minioUtil.getAllBuckets()) {
            log.info("Bucket名称为==={}", b.name());
        }
    }

    @Test
    public void testUtil() {
        System.out.println(UUID.randomUUID().toString().replaceAll("-", ""));
        System.out.println(DateUtil.format(new Date(), "yyyy-MM/dd"));
    }

    @Test
    public void testFileDelete() {
        boolean ans = minioUtil.remove("2024-05/29/e82bdf2e0c734d9699b9d045a42305b7.pptx");
        if (ans) {
            System.out.println("文件删除成功");
        }
    }

    @Test
    public void testImgPreview() {
        //获取图片访问地址需要使用全文件名换取url
        String previewUrl = minioUtil.preview("2024-05/29/f19a1a04c61742e790ab302e56d5b9f0.jpg");
        log.info("图片预览地址{}", previewUrl);
    }

    @Test
    public void testBucketInfo() {
        List<Item> items = minioUtil.listObjects();
        System.out.println(items);
    }

    @Test
    public void testBaseEmail() {
        String to = "lxqaxx@163.com";
        //String to = "1255407198@qq.com";
        String subject = "邮件发送测试标题";
        if (emailService.sendRemindEmail1(to, subject, "测试标题", "测试邮件正文内容")) {
            System.out.println("邮件发送成功！");
        } else {
            System.out.println("发送失败啦~~~");
        }
    }

    @Test
    public void testCalendar() {
        Calendar instance = Calendar.getInstance();
        Date date = instance.getTime();
        System.out.println(date);

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
        for (SysMemo s : sysMemos) {
            log.info(s.toString());
        }
    }

    @Test
    public void testAuth() {
        Long loginUserId = loginUserInfoUtil.getLoginUserId();
    }

    @Test
    public void testPassword() {
//        log.info(bCryptPasswordEncoder.encode("Suam3520"));
//        log.info(bCryptPasswordEncoder.encode("lxqaxx"));
        System.out.println(bCryptPasswordEncoder.encode("Suam3520"));
        System.out.println(bCryptPasswordEncoder.encode("lxqaxx"));
    }

    @Test
    public void testImgEmail() throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String from = "1255407198@qq.com";
        String to = "1255407198@qq.com";
        String subject = "邮件主题";

        String htmlContent = "<html>"
                + "<body style='background-color: #f4f4f4;'>"
                + "<div style='text-align: center;'>"
                + "<img src='cid:backgroundImage' style='width: 100%; height: auto;' />"
                + "<div style='position: absolute; top: 20px; left: 20px; color: white;'>"
                + "<h1>这是带有背景图片的邮件</h1>"
                + "<p>邮件内容...</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // 添加内联背景图片
        ClassPathResource resource = new ClassPathResource("/imgs/5268d877a2a04864b36b4961ab793f4f.jpg");
        helper.addInline("backgroundImage", resource);

        javaMailSender.send(message);
    }

    @Test
    public void testSplit(){
        String ss = "123/5698//51/kkl/aacn";
        System.out.println(Arrays.asList(ss.split("/")));
    }
}
