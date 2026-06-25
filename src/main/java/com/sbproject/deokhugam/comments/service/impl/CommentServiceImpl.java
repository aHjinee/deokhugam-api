package com.sbproject.deokhugam.comments.service.impl;

import com.sbproject.deokhugam.comments.repository.CommentRepository;
import com.sbproject.deokhugam.comments.service.CommentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
}
