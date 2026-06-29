package com.sbproject.deokhugam.storage.impl;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.uuid.Generators;
import com.sbproject.deokhugam.storage.FileStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class FileStorageS3 implements FileStorage {

	// S3 저장 경로 prefix (attachments/파일명)
	private static final String PREFIX = "attachments";
	private final S3Client s3Client;       // S3 업로드/삭제용
	private final S3Presigner s3Presigner; // Presigned URL 생성용
	@Value("${aws.s3.bucket}")
	private String bucket; // S3 버킷명
	@Value("${aws.s3.presigned-url-expiration}")
	private long presignedUrlExpiration; // Presigned URL 만료 시간 (초)

	@Override
	public String save(MultipartFile file) {
		String original = file.getOriginalFilename();
		String ext = (original != null && original.contains("."))
			? original.substring(original.lastIndexOf('.'))
			: "";

		String key = PREFIX + "/" + Generators.timeBasedEpochGenerator().generate() + ext;

		try {
			// 업로드용 객체 생성
			PutObjectRequest putReq = PutObjectRequest.builder()
			                                          .bucket(bucket)
			                                          .key(key)
			                                          .contentType(file.getContentType())
			                                          .build();
			s3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize())); // S3 업로드
			log.info("S3 업로드 완료: {}", key);
		} catch (IOException e) {
			throw new RuntimeException("S3 업로드 실패: " + key, e);
		}
		return key; // DB 저장용 storageKey
	}

	@Override
	public void delete(String storageKey) {
		try {
			// 삭제 요청
			s3Client.deleteObject(DeleteObjectRequest.builder()
			                                         .bucket(bucket)
			                                         .key(storageKey)
			                                         .build());
			log.info("S3 삭제 완료: {}", storageKey);
		} catch (Exception e) {
			log.warn("S3 삭제 실패 (무시 가능): {}", storageKey, e);
		}
	}

	// 다운로드 가능한 URL 경로 가져오기 (제한 10분)
	@Override
	public String getUrl(String storageKey) {
		// 요청 객체 생성
		GetObjectRequest getReq = GetObjectRequest.builder()
		                                          .bucket(bucket)
		                                          .key(storageKey)
		                                          .build();

		GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
		                                                            .signatureDuration(Duration.ofSeconds(
			                                                            presignedUrlExpiration)) // 만료 시간 (기본 10분)
		                                                            .getObjectRequest(getReq)
		                                                            .build();

		// 임시 접근 Presigned URL 반환
		return s3Presigner.presignGetObject(presignReq).url().toExternalForm();
	}
}
