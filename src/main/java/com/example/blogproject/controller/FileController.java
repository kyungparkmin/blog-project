package com.example.blogproject.controller;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class FileController {
    @Value("${file.dir}")
    private String fileDir;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 s3client;


    @PostMapping("/image/upload")
    @ResponseBody
    public ModelAndView image(MultipartHttpServletRequest request, @RequestPart(value="upload", required = false) MultipartFile image) throws Exception {
        ModelAndView mv = new ModelAndView("jsonView");

        MultipartFile uploadFile = request.getFile("upload");

        String originalFileName = uploadFile.getOriginalFilename();

        String ext = originalFileName.substring(originalFileName.indexOf("."));

        String newFileName = UUID.randomUUID() + ext;

        String realPath = request.getServletContext().getRealPath("/");
        System.out.println(realPath);

        String savePath = "\\img\\" + newFileName;

        String uploadPath = fileDir + "\\" + newFileName;

        System.out.println(savePath);
        System.out.println(uploadPath);

        File file = new File(uploadPath);

        uploadFile.transferTo(file);

        mv.addObject("uploaded", true);
        mv.addObject("url", savePath);

        return mv;
    }




    @PostMapping("/upload")
    @ResponseBody
    public ModelAndView upload(MultipartHttpServletRequest  request) throws IOException {
        ModelAndView mv = new ModelAndView("jsonView");

        MultipartFile file = request.getFile("upload");
        String filename = file.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        String key = UUID.randomUUID().toString() + "." + extension;

        s3client.putObject(bucketName, key, file.getInputStream(), null);

        String url = s3client.getUrl(bucketName, key).toString();


        mv.addObject("uploaded", true);
        mv.addObject("url", url);

        return mv;
    }
}
