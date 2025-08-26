package org.example.backend.repository;


import org.example.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT p FROM Post p WHERE p.deletedDate IS NOT NULL AND p.author.username = :username")
    List<Post> findDeletedByUsername(@Param("username") String username);

    @Query("SELECT p FROM Post p WHERE p.deletedDate IS NOT NULL")
    List<Post> findAllDeleted();

    List<Post> findAllByAuthorIdAndDeletedDateIsNullOrderByCreatedDateDesc(UUID authorId);


    @Query("SELECT p FROM Post p WHERE p.deletedDate IS NULL AND p.author.username = :username")
    List<Post> findActiveByUsername(@Param("username") String username);

    @Query("SELECT p FROM Post p WHERE p.deletedDate IS NULL")
    List<Post> findAllActive();

    /** {@code @Override} на "мягкое" удаление */
    @Override
    default void deleteById(@NonNull UUID id) {
        findById(id).ifPresent(post -> {
            post.setDeletedDate(LocalDateTime.now());
            save(post);
        });
    }
}
