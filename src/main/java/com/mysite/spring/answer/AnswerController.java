package com.mysite.spring.answer;

import com.mysite.spring.question.Question;
import com.mysite.spring.question.QuestionService;
import com.mysite.spring.user.SiteUser;
import com.mysite.spring.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    private void verifyOwnership(Answer answer, String username) {
        if (!answer.getAuthor().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "권한이 없습니다.");
        }
    }

    // 댓글 작성
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,
                               @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }
        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }

    // 댓글 수정
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = this.answerService.getAnswer(id);
        verifyOwnership(answer, principal.getName());
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = this.answerService.getAnswer(id);
        verifyOwnership(answer, principal.getName());
        this.answerService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }

    // 댓글 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        verifyOwnership(answer, principal.getName());
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    // 댓글 추천
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.answerService.vote(answer, siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
    }

    // 답변 페이징 및 정렬
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{id}")
    public String list(Model model, @PathVariable("id") Integer id,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "sort", defaultValue = "createDate") String sort) {
        Question question = this.questionService.getQuestion(id);
        Page<Answer> paging = this.answerService.getAnswerListByQuestion(question, page, sort);
        model.addAttribute("paging", paging);
        model.addAttribute("question", question);
        return "question_detail";
    }
}
