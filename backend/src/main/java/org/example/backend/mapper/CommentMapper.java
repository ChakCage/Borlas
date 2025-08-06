package org.example.backend.mapper;

import org.example.backend.dto.CommentResponse;
import org.example.backend.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponse toDto(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedBy(comment.getCreatedBy().getId());
        dto.setCreatedByName(comment.getCreatedBy().getUsername());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setEditedDate(comment.getEditedDate());
        dto.setDeletedDate(comment.getDeletedDate());
        dto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        return dto;
    }
}
