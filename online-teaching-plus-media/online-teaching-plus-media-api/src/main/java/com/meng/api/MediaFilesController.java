package com.meng.api;

import com.meng.model.PageParams;
import com.meng.model.PageResult;
import com.meng.model.dto.QueryMediaParamsDto;
import com.meng.model.dto.UploadFileParamsDto;
import com.meng.model.dto.UploadFileResultDto;
import com.meng.model.po.MediaFiles;
import com.meng.media.service.MediaFileService;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
 public class MediaFilesController {

     @Autowired
     MediaFileService mediaFileService;
     @ApiOperation("媒资列表查询接口")
     @PostMapping("/files")
     public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
         Long companyId = 1232141425L;
         return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);
     }

     /**
      * @description 上传文件接口层。
      * * @param upload
      * * @param folder
      * * @param objectName
      * * @return com.meng.model.dto.UploadFileResultDto
      * * @author 梦举
      * * @date 2023/4/8 10:30
      * */
     @ApiOperation("上传文件")
     @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     @ResponseBody
     public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile upload, @RequestParam(value = "folder", required = false) String folder, @RequestParam(value = "objectName", required = false) String objectName) throws IOException{
         String contentType = upload.getContentType();
         Long companyId = 1232141425L;
         UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
         uploadFileParamsDto.setFileSize(upload.getSize());
         if (contentType.indexOf("image") >= 0) {
            // 图片
            uploadFileParamsDto.setFileType("001001");
         } else {
             // 其他
             uploadFileParamsDto.setFileType("001003");
         }
         uploadFileParamsDto.setRemark("");
         String fileName = new String(upload.getOriginalFilename().getBytes("GBK"), "utf-8");
         uploadFileParamsDto.setFilename(fileName);
         uploadFileParamsDto.setContentType(contentType);
         return mediaFileService.uploadFile(companyId, uploadFileParamsDto, upload.getBytes(), folder, objectName);
    }

 }
