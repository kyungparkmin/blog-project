package com.example.blogproject.comment;

import com.example.blogproject.post.Post;
import com.example.blogproject.post.PostService;
import com.example.blogproject.user.SiteUser;
import com.example.blogproject.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class CommentController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/create/{id}")
    public String saveComment(@PathVariable("id") Long id, @RequestParam String content, Principal principal) {
        Post post = this.postService.findOne(id);

        SiteUser user = this.userService.getUser(principal.getName());

        this.commentService.create(post, content, user);

        return String.format("redirect:/post/%s", id);
    }
}
