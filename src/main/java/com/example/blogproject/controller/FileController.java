package com.example.blogproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FileController {
    @Value("${file.dir}")
    private String fileDir;

    @PostMapping("/image/upload")
    @ResponseBody
    public ModelAndView image(MultipartHttpServletRequest request) throws Exception {

        ModelAndView mv = new ModelAndView("jsonView");

        MultipartFile uploadFile = request.getFile("upload");

        String originalFileName = uploadFile.getOriginalFilename();

        String ext = originalFileName.substring(originalFileName.indexOf("."));

        String newFileName = UUID.randomUUID() + ext;

        String realPath = request.getServletContext().getRealPath("/");
        System.out.println(realPath);

        String savePath = fileDir + "\\" + newFileName;

        String uploadPath = fileDir + "\\" + newFileName;

        File file = new File(savePath);

        uploadFile.transferTo(file);

        mv.addObject("uploaded", true);
        mv.addObject("url", uploadPath);

        return mv;
    }
}
