package com.mysite.spring.question;

import com.mysite.spring.answer.Answer;
import com.mysite.spring.category.Category;
import com.mysite.spring.user.SiteUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    //질문이 삭제되면 모든 답변이 삭제되도록 cascade를 REMOVE해주기
    @OneToMany(mappedBy = "question",cascade=CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany //추천이 서로 중복되지 않도록 Set 자료형 사용
    Set<SiteUser> voter;

    @ManyToOne
    private Category category;

    @Column(columnDefinition = "integer default 0")
    @NotNull
    private Integer view;
}
