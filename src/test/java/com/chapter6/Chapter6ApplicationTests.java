package com.chapter6;

import com.chapter6.mapper.UriMapper;
import com.chapter6.model.ApiUtilData;
import com.chapter6.model.request.RequestDoTest;
import com.chapter6.time.MailServer;
import com.chapter6.util.ApiUtil;
import com.chapter6.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
class Chapter6ApplicationTests {

    @Autowired
    private TestUtil testUtil;
    @Autowired
    private UriMapper uriMapper;
    @Autowired
    private MailServer mailServer;

    @Test
    void contextLoads() throws Throwable {
        mailServer.sendSimpleMail("513653423@qq.com","主题：你好普通邮件","内容：第一封邮件");
    }

}
