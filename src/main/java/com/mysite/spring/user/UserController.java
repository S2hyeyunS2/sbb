package com.mysite.spring.user;

import com.mysite.spring.DataNotFoundException;
import com.mysite.spring.answer.AnswerService;
import com.mysite.spring.question.QuestionService;
import com.mysite.spring.user.dto.TempPasswordForm;
import com.mysite.spring.user.exception.EmailException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private static final String TEMP_PASSWORD_FORM="temp_password_form";

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/tempPassword")
    public String showTempPasswordForm(Model model) {
        model.addAttribute("tempPasswordForm", new TempPasswordForm());
        return "temp_password_form";  // 템플릿 파일 temp_password_form.html을 반환
    }

    @PostMapping("/tempPassword")
    public String sendTempPassword(@Valid TempPasswordForm tempPasswordForm,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return TEMP_PASSWORD_FORM;
        }

        try {
            userService.modifyPassword(tempPasswordForm.getEmail());
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            bindingResult.reject("emailNotFound", e.getMessage());
            return TEMP_PASSWORD_FORM;
        } catch (EmailException e) {
            e.printStackTrace();
            bindingResult.reject("sendEmailFail", e.getMessage());
            return TEMP_PASSWORD_FORM;
        }
        return "redirect:/";
    }
}
