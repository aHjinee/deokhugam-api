package com.sbproject.deokhugam.comments.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sbproject.deokhugam.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
@SuperBuilder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

	@Column(name = "review_id", nullable = false, columnDefinition = "uuid")
	private UUID reviewId;

	@Column(name = "user_id", nullable = false, columnDefinition = "uuid")
	private UUID userId;

	@Column(nullable = false, length = 500)
	private String content;
}
