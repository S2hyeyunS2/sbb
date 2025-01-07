package com.mysite.spring.comment;

import com.mysite.spring.DataNotFoundException;
import com.mysite.spring.answer.Answer;
import com.mysite.spring.question.Question;
import com.mysite.spring.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    // 질문에 대한 답글을 생성
    public Comment createQuestionComment(Question question, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setQuestion(question);
        comment.setAuthor(author);
        this.commentRepository.save(comment);
        return comment;
    }

    // 질문에 대한 답글을 가져옴
    public Comment getQuestionComment(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    // 답변에 대한 답글을 생성
    public Comment createAnswerComment(Answer answer, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAnswer(answer);
        comment.setAuthor(author);
        this.commentRepository.save(comment);
        return comment;
    }

    // 답변에 대한 답글을 가져옴
    public Comment getAnswerComment(Integer id) {
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    // 삭제 기능
    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }

    // 수정 기능
    public void modify(Comment comment,String content){
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }
    // 추천 기능
    public void vote(Comment comment, SiteUser siteUser){
        comment.getVoter().add(siteUser);
        this.commentRepository.save(comment);
    }

    // 유저 아이디를 찾는 기능
    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByAuthorId(userId);
    }
}
