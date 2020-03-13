package com.chapter6;

import com.chapter6.mapper.UriMapper;
import com.chapter6.model.ApiUtilData;
import com.chapter6.model.request.RequestDoTest;
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

    @Test
    void contextLoads() throws Throwable {
        String s = "a";
        String[] ss = s.split(",");
        System.out.println(ss[0]);

    }

}
