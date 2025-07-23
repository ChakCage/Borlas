package org.example.backend.mapper;

import org.example.backend.dto.PostDto;
import org.example.backend.dto.PostResponseDto;
import org.example.backend.model.Post;
import org.example.backend.model.User;

import java.time.LocalDateTime;

/* PostMapper.java */
public final class PostMapper {
    private PostMapper(){}
    public static Post toEntity(PostDto d, User author){
        Post p=new Post();
        p.setTitle(d.getTitle());
        p.setContent(d.getContent());
        p.setAuthor(author);
        return p;
    }
    public static void update(Post p,PostDto d){
        p.setTitle(d.getTitle());
        p.setContent(d.getContent());
        p.setUpdatedDate(LocalDateTime.now());
    }
    public static PostResponseDto toDto(Post p){
        PostResponseDto r=new PostResponseDto();
        r.setId(p.getId()); r.setTitle(p.getTitle()); r.setContent(p.getContent());
        r.setCreatedDate(p.getCreatedDate()); r.setUpdatedDate(p.getUpdatedDate());
        r.setAuthorId(p.getAuthor().getId());
        return r;
    }
}
