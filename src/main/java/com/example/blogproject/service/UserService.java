package com.example.blogproject.service;

import com.example.blogproject.entity.SiteUser;
import com.example.blogproject.exception.DataNotFoundException;
import com.example.blogproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(String username, String password1, String name) {
        SiteUser user = new SiteUser();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password1));
        user.setName(name);

        this.userRepository.save(user);
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("user not found");
        }
    }
}


