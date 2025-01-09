package com.mysite.spring;

import com.mysite.spring.question.QuestionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest //스프링부트 테스트 클래스
class ApplicationTests {

    @Autowired //의존성 주입(DI) , questionRepository 객체를 자동으로 만들어 줌
    private QuestionService questionService;

    @Transactional
    @Test //testJpa 메서드가 테스트 메서드임을 나타냄
    void testJpa() {
    }
}
