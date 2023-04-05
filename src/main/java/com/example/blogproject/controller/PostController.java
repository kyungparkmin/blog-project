package com.example.blogproject.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.blogproject.config.S3Client;
import com.example.blogproject.entity.Post;
import com.example.blogproject.dto.PostDTO;
import com.example.blogproject.service.PostService;
import com.example.blogproject.entity.SiteUser;
import com.example.blogproject.service.S3Service;
import com.example.blogproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final UserService userService;
    private final PostService postService;

    private final S3Service s3Service;

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
    @ResponseBody
    public String savePost(PostDTO postDTO, Principal principal, @RequestParam("file") MultipartFile file) throws IOException {
        String url = s3Service.upload(file);
        SiteUser user = this.userService.getUser(principal.getName());

        this.postService.save(postDTO.getTitle(), postDTO.getContent(), user);

        return "redirect:/";
    }

    @GetMapping("/")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value="q", defaultValue = "") String q) {
        Page<Post> paging = this.postService.getList(page, q);

        model.addAttribute("paging", paging);
        model.addAttribute("q", q);

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
    @GetMapping("/post/modify/{id}")
    public String modifyPost(PostDTO postDTO, @PathVariable("id") Long id, Principal principal) {
        Post post = this.postService.findOne(id);

        if(!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
        }

        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());

        return "modify-page";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post/modify/{id}")
    public String modifyPost(Principal principal, PostDTO postDTO, @PathVariable("id") Long id) {
        Post post = this.postService.findOne(id);

        if(!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
        }

        this.postService.modify(post, postDTO.getTitle(), postDTO.getContent());


        return String.format("redirect:/post/%s", id);
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
