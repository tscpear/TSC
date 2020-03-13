package com.chapter6.controller;

import com.chapter6.mapper.UserMapper;
import com.chapter6.model.request.RequestUser;
import oracle.jrockit.jfr.events.RequestableEventEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;




@Controller
public class LoginController {

    @Autowired
    private UserMapper userMapper;


    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("msg", "爱你哦");
        return "index";
    }

    @RequestMapping("/index")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {

        String token = UUID.randomUUID()+"";
        RequestUser requestUser = new RequestUser();
        requestUser.setUsername(username);
        requestUser.setPassword(password);
        requestUser.setToken(token);
        userMapper.insertToken(requestUser);

        //具体的业务
        if ((!StringUtils.isEmpty(username)) && password.equals("123456")) {
            model.addAttribute("msg", "登入成功");
            return "index";
        } else {
            model.addAttribute("msg", "密码错误");
            return "login";
        }
    }

    @RequestMapping("/test")
    public String test() {
        return "index";
    }
}
