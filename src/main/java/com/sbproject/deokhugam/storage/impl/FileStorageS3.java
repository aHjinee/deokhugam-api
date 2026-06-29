package com.sbproject.deokhugam.storage.impl;

import com.fasterxml.uuid.Generators;
import com.sbproject.deokhugam.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class FileStorageS3 implements FileStorage {

	@Override
	public String save(MultipartFile file) {
		return "";
	}

	@Override
	public void delete(String storageKey) {

	}

	@Override
	public String getUrl(String storageKey) {
		return "";
	}
}
