package com.example.blogproject.post;

import com.example.blogproject.user.SiteUser;
import com.example.blogproject.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
}
