package com.sbproject.deokhugam.storage.impl;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.uuid.Generators;
import com.sbproject.deokhugam.config.FileConfig;
import com.sbproject.deokhugam.storage.FileStorage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class FileStorageDev implements FileStorage {
	private final FileConfig fileConfig;

	@Value("${server.port:8080}")
	private int serverPort;

	@Override
	public String save(MultipartFile file) {
		String original = file.getOriginalFilename();
		String ext = (original != null && original.contains("."))
			? original.substring(original.lastIndexOf('.'))
			: "";
		String key = Generators.timeBasedEpochGenerator().generate() + ext;

		try {
			file.transferTo(fileConfig.getRootPath().resolve(key));
		} catch (IOException e) {
			throw new RuntimeException("로컬 파일 저장 실패: " + key, e);
		}
		return key;
	}

	@Override
	public void delete(String storageKey) {
		try {
			Files.deleteIfExists(fileConfig.getRootPath().resolve(storageKey));
		} catch (IOException e) {
			throw new RuntimeException("로컬 파일 삭제 실패: " + storageKey, e);
		}
	}

	@Override
	public String getUrl(String storageKey) {
		return "http://localhost:" + serverPort + "/files/" + storageKey;
	}

}
