package com.example.blogproject.service;

import com.example.blogproject.entity.File;
import com.example.blogproject.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public void save(String url) {
        File file = new File();

        file.setUrl(url);

        this.fileRepository.save(file);
    }
}
