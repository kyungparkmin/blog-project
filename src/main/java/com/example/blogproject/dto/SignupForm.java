package com.example.blogproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupForm {
    @NotEmpty(message = "아이디를 입력해 주세요")
    private String username;

    @NotEmpty(message = "비밀번호를 입력해 주세요")
    private String password1;

    @NotEmpty(message = "비밀번호확인을 입력해 주세요")
    private String password2;

    @NotEmpty(message = "이름을 입력해 주세요")
    private String name;
}