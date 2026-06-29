package com.sbproject.deokhugam.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface FileStorage {
	public String save(MultipartFile file);

	public void delete(String storageKey);

	public String getUrl(String storageKey);
}
