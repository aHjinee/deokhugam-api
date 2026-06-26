package com.sbproject.deokhugam.comments.repository;

import java.util.Optional;
import java.util.UUID;

import com.sbproject.deokhugam.comments.entity.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

	Optional<Comment> findByIdAndDeletedAtIsNull(UUID id);
}
