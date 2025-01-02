package com.mysite.spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/sbb")
    @ResponseBody
    public String index() {
        return "안녕하세요 sbb에 오신것을 환영합니다.";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/question/list"; // /question/list URL로 페이지를 리다이렉팅 하라는 명령어
                                          // 리다이렉트란 클라이언트가 요청하면 새로운 URL로 전송하는 것
    }

}
