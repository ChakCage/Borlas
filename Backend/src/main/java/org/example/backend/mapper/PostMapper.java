package org.example.backend.mapper;

import org.example.backend.dto.PostRequest;
import org.example.backend.dto.PostResponse;
import org.example.backend.model.Post;
import org.example.backend.model.User;

public class PostMapper {
    public static Post toEntity(PostRequest dto, User author) {
        Post p = new Post();
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setAuthor(author);
        return p;
    }
    public static PostResponse toDto(Post p) {
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
