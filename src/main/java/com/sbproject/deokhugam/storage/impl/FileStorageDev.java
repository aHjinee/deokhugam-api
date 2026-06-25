package com.sbproject.deokhugam.storage.impl;

import com.fasterxml.uuid.Generators;
import com.sbproject.deokhugam.config.FileConfig;
import com.sbproject.deokhugam.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
public class FileStorageDev implements FileStorage {

}
