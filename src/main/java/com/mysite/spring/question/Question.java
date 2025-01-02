package com.mysite.spring.question;

import com.mysite.spring.answer.Answer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity //Question 클래스를 엔티티로 인식
@Table(name="question")
public class Question {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) //유효한 값으로, 중복되면 안됨
    private Integer id;

    @Column(length=200)
    private String subject;

    @Column(columnDefinition="TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question",cascade=CascadeType.REMOVE)
    private List<Answer> answerList;
}
