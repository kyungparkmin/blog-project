package com.example.blogproject.controller;

import com.example.blogproject.entity.Post;
import com.example.blogproject.dto.PostDTO;
import com.example.blogproject.service.PostService;
import com.example.blogproject.entity.SiteUser;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final UserService userService;
    private final PostService postService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post")
    public String write() {
        return "write-page";
    }

    @GetMapping("post/{id}")
    public String getPost(Model model, @PathVariable Long id) {
        Post post = this.postService.findOne(id);
        postService.updateViews(id);

        model.addAttribute("post", post);

        return "post_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post/save")
    public String savePost(PostDTO postDTO, Principal principal) {
        SiteUser user = this.userService.getUser(principal.getName());

        this.postService.save(postDTO.getTitle(), postDTO.getContent(), user);

        return "redirect:/";
    }

    @GetMapping("/")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Post> paging = this.postService.getList(page);

        model.addAttribute("paging", paging);

        return "main";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/remove/{id}")
    public String removePost(Principal principal, @PathVariable("id") Long id) {
        Post post = this.postService.findOne(id);

        if(!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
        }

        this.postService.remove(post);

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/like/{id}")
    public String postLike(Principal principal, @PathVariable("id") Long id) {
        Post post = this.postService.findOne(id);
        SiteUser user = this.userService.getUser(principal.getName());
        this.postService.like(post, user);
        return String.format("redirect:/post/%s", id);
    }
}
