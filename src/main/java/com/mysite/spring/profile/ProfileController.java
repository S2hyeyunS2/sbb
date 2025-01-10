package com.mysite.spring.profile;

import com.mysite.spring.answer.Answer;
import com.mysite.spring.answer.AnswerService;
import com.mysite.spring.comment.Comment;
import com.mysite.spring.comment.CommentService;
import com.mysite.spring.question.Question;
import com.mysite.spring.question.QuestionService;
import com.mysite.spring.user.SiteUser;
import com.mysite.spring.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/profile")
    public String profile(Model model, @RequestParam(value = "questionPage", defaultValue = "0") int questionPage,
                          @RequestParam(value = "answerPage", defaultValue = "0") int answerPage,
                          @RequestParam(value = "commentPage", defaultValue = "0") int commentPage,
                          @AuthenticationPrincipal UserDetails detail) {

        SiteUser siteUser=userService.getUser(detail.getUsername());

        // 로그인한 사용자의 정보를 가져옴
        model.addAttribute("siteUser", siteUser);

        // 로그인한 사용자가 작성한 질문
        Page<Question> questionPaging = questionService.getUserQuestions(siteUser, questionPage);
        model.addAttribute("questionPaging", questionPaging);

        // 로그인한 사용자가 작성한 답변
        Page<Answer> answerPaging = answerService.getUserAnswers(siteUser, answerPage);
        model.addAttribute("answerPaging", answerPaging);

        // 로그인한 사용자가 작성한 댓글
        Page<Comment> commentPaging = commentService.getUserComments(siteUser, commentPage);
        model.addAttribute("commentPaging", commentPaging);

        return "profile";
    }
}
