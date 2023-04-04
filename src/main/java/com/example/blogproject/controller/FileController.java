package com.example.blogproject.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.example.blogproject.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FileController {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 s3client;
    private final FileService fileService;

    @PostMapping("/upload")
    @ResponseBody
    public ModelAndView upload(MultipartHttpServletRequest request) throws IOException {
        ModelAndView mv = new ModelAndView("jsonView");

        MultipartFile file = request.getFile("upload");
        System.out.println(file);
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        String key = UUID.randomUUID() + "." + extension;

        System.out.println(key);

        s3client.putObject(bucketName, key, file.getInputStream(), null);

        String url = s3client.getUrl(bucketName, key).toString();

        mv.addObject("uploaded", true);
        mv.addObject("url", url);

        fileService.save(url);

        return mv;
    }
}
