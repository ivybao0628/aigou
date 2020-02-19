package com.aigou.controller;

import com.aigou.util.FastDFSClient;
import com.aigou.util.FastDFSFile;
import entity.Result;
import entity.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author anchao
 * @date 2020/2/18 21:32
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("upload")
public class FileUploadController {

    /**
     * 文件上传
     */
    @PostMapping
    public Result upload(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        log.info("FileUploadController.file.OriginalFilename={}",originalFilename);
        String filenameExtension = StringUtils.getFilenameExtension(originalFilename);
        try {
            FastDFSFile fileInfo = new FastDFSFile(file.getOriginalFilename(), file.getBytes(), filenameExtension);
            log.info("FileUploadController.file.fileInfo={}",fileInfo);
            String[] uploadResult = FastDFSClient.upload(fileInfo);
            String url = FastDFSClient.getTrackerUrl()+uploadResult[0]+"/"+uploadResult[1];
            log.info("FileUploadController.upload={}", url);
            return new Result(true, StatusCode.OK, "上传成功！",url);
        } catch (IOException e) {
            log.error("FileUploadController.upload.error=", e);
        }
        return new Result(false, StatusCode.ERROR, "上传失败！");
    }

}
