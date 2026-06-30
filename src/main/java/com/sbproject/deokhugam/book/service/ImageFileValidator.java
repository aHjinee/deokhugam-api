package com.sbproject.deokhugam.book.service;

import java.io.IOException;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sbproject.deokhugam.book.exception.InvalidImageException;

@Component
public class ImageFileValidator {

	private static final long MAX_SIZE = 10 * 1024 * 1024;
	private static final Set<String> ALLOWED = Set.of(
		"image/png",
		"image/jpeg",
		"image/jpg",
		"image/webp",
		"image/heic",
		"image/heif"
	);
	private final Tika tika = new Tika();

	public void validate(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw InvalidImageException.withReason("이미지가 비어 있습니다.");
		}
		if (file.getSize() > MAX_SIZE) {
			throw InvalidImageException.withReason("이미지 크기는 10MB를 초과할 수 없습니다.");
		}
		String contentType = detectType(file);
		if (contentType == null || !ALLOWED.contains(contentType.toLowerCase())) {
			throw InvalidImageException.withReason("지원하지 않는 이미지 형식입니다: " + contentType);
		}
	}

	private String detectType(MultipartFile file) {
		try {
			return tika.detect(file.getInputStream());
		} catch (IOException e) {
			throw InvalidImageException.withReason("이미지 파일을 읽을 수 없습니다.");
		}
	}
}
