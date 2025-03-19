package com.fastcampus.sns.controller.response;

import com.fastcampus.sns.model.Comment;
import com.fastcampus.sns.model.entity.CommentEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class PostCommentResponse {
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;

    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static PostCommentResponse fromComment(Comment comment) {
        return new PostCommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUserName(),
                comment.getPostId(),
                comment.getRegisteredAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt()
        );
    }

}
