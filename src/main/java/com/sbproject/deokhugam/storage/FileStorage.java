package com.sbproject.deokhugam.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
	String save(MultipartFile file);

	void delete(String storageKey);

	String getUrl(String storageKey);
}
