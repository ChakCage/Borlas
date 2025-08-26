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

    public PostResponse toDto(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCreatedDate(post.getCreatedDate());
        response.setUpdatedDate(post.getUpdatedDate());
        response.setAuthorId(post.getAuthor().getId());
        response.setAuthorUsername(post.getAuthor().getUsername());
        return response;
    }
}
