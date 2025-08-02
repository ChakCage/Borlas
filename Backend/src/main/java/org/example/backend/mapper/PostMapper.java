package org.example.backend.mapper;

import org.example.backend.dto.PostRequest;
import org.example.backend.dto.PostResponse;
import org.example.backend.model.Post;
import org.example.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(PostRequest dto, User author) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(author);
        return post;
    }

    public PostResponse toDto(Post p) {
        PostResponse r = new PostResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setContent(p.getContent());
        r.setCreatedDate(p.getCreatedDate());
        r.setUpdatedDate(p.getUpdatedDate());
        r.setAuthorId(p.getAuthor().getId());
        return r;
    }
}
