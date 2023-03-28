package com.example.blogproject.service;

import com.example.blogproject.dto.PostDTO;
import com.example.blogproject.exception.DataNotFoundException;
import com.example.blogproject.entity.Post;
import com.example.blogproject.repository.PostRepository;
import com.example.blogproject.entity.SiteUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void save(String title, String content, SiteUser user) {
        Post post = new Post();

        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(user);

        this.postRepository.save(post);
    }

    public Post findOne(Long id) {
        Optional<Post> post = this.postRepository.findById(id);

        if(post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException("글을 찾을 수 없습니다");
        }
    }

    public Page<Post> getList(int page) {
        Pageable pageable = PageRequest.of(page, 9);

        return this.postRepository.findAllByOrderByIdDesc(pageable);
    }

    public void like(Post post, SiteUser user) {
        post.getLikes().add(user);
        this.postRepository.save(post);
    }

    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);

        this.postRepository.save(post);
    }


    public void remove(Post post) {
        this.postRepository.delete(post);
    }

    @Transactional
    public int updateViews(Long id) {
        return postRepository.updateViews(id);
    }
}