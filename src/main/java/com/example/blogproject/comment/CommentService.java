package com.example.blogproject.comment;

import com.example.blogproject.post.Post;
import com.example.blogproject.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public void create(Post post, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(author);

        this.commentRepository.save(comment);
    }
}
