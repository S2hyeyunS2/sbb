package com.mysite.spring;

import com.mysite.spring.answer.Answer;
import com.mysite.spring.answer.AnswerRepository;
import com.mysite.spring.question.Question;
import com.mysite.spring.question.QuestionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest //스프링부트 테스트 클래스
class ApplicationTests {

    @Autowired //의존성 주입(DI) , questionRepository 객체를 자동으로 만들어 줌
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Transactional
    @Test //testJpa 메서드가 테스트 메서드임을 나타냄
    void testJpa() {
        Optional<Question> oq = this.questionRepository.findById(2);
        assertTrue(oq.isPresent());
        Question q = oq.get();

        List<Answer> answerList = q.getAnswerList();

        assertEquals(1, answerList.size());
        assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
    }
}
