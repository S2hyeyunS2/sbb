package com.mysite.spring.question;

import com.mysite.spring.DataNotFoundException;
import com.mysite.spring.answer.Answer;
import com.mysite.spring.category.Category;
import com.mysite.spring.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList(){ // 질문 목록 데이터를 조회하여 리턴
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);
        if(question.isPresent()){
            return question.get();
        } else{
            throw new DataNotFoundException("question not found");
        }
    }

    public Page<Question> getList(int page, String kw, String categoryName) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        // 기본 Specification 생성
        Specification<Question> spec = Specification.where(null);

        // 키워드 및 카테고리 검색 조건 추가
        if ((kw != null && !kw.isEmpty()) || (categoryName != null && !categoryName.isEmpty())) {
            spec = spec.and(search(kw, categoryName)); // search 메서드 호출하여 조건 추가
        }

        // 필터링된 결과를 페이징하여 반환
        return this.questionRepository.findAll(spec, pageable);
    }


    public void create(String subject, String content, SiteUser user, Category category) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        q.setCategory(category);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw, String categoryName) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                // 엔티티 간의 조인 (질문 작성자, 답변, 카테고리 등)
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);  // 질문 작성자
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);  // 답변
                Join<Question, Category> c = q.join("category", JoinType.LEFT);  // 카테고리
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);     // 답변 작성자

                // 조건 생성
                Predicate predicate = cb.or(
                        cb.like(q.get("subject"), "%" + kw + "%"),  // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),  // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"), // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),  // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%")  // 답변 작성자
                );

                // 카테고리 필터링 조건 (categoryName이 비어 있지 않으면 적용)
                if (!categoryName.isEmpty()) {
                    predicate = cb.and(predicate, cb.like(c.get("name"), "%" + categoryName + "%"));  // 카테고리 이름
                }

                return predicate;
            }
        };
    }
}