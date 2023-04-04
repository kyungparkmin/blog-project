package com.example.blogproject.service;

import com.example.blogproject.entity.Post;
import com.example.blogproject.entity.SiteUser;
import com.example.blogproject.exception.DataNotFoundException;
import com.example.blogproject.repository.PostRepository;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private Specification<Post> search(String q) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Post, SiteUser> u = root.join("author", JoinType.LEFT);
                return cb.or(cb.like(root.get("title"), "%" + q + "%"),
                        cb.like(root.get("content"), "%" + q + "%"),
                        cb.like(u.get("name"), "%" + q + "%"));
            }
        };
    }

    public void save(String title, String content, SiteUser user) {
        Post post = new Post();

        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(user);

        this.postRepository.save(post);
    }

    public Post findOne(Long id) {
        Optional<Post> post = this.postRepository.findById(id);

        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException("글을 찾을 수 없습니다");
        }
    }

    public Page<Post> getList(int page, String q) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdDate"));
        Pageable pageable = PageRequest.of(page, 9, Sort.by(sorts));

        Specification<Post> spec = search(q);

        return this.postRepository.findAll(spec, pageable);
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