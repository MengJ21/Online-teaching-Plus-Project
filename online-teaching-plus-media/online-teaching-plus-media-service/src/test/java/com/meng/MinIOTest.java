package com.meng;

import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author 梦举
 * @version 1.0
 * @description 测试MinIO
 * @date 2023/4/6 20:20
 */

public class MinIOTest {
    static MinioClient minioClient = MinioClient.builder()
            .endpoint("http://123.57.151.75:9000")
            .credentials("minioadmin", "minioadmin")
            .build();
    /**
    * @description 测试上传文件。
    *
    * @return
    * @author 梦举
    * @date 2023/4/6 20:25
    */
    public static void upload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("test").build());
            // 检查test桶是否创建
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("test").build());
            } else {
                System.out.println("Bucket 'test' already exists");
            }
            // 上传视频
            minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket("test")
                    .object("艺妓回忆录.mp4")
                    .filename("D:\\videos\\艺妓回忆录.mp4")
                    .build());
            // 上传音乐
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("test")
                            .object("告五人 - 唯一.mp3")
                            .filename("D:\\CloudMusic\\music\\CloudMusic\\告五人 - 唯一.mp3")
                            .build()
            );
            System.out.println("上传成功");
        } catch (MinioException e) {
            System.out.println("Error occured: " + e);
            System.out.println("Http trace：" + e.httpTrace());
        }
    }

    public static void delete(String bucket, String filepath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(filepath).build());
            System.out.println("删除成功");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("Http trace: " + e.httpTrace());
        }
    }
    /**
    * @description 下载文件。
    * @param bucket
     * @param filepath
     * @param outFile
    * @return void
    * @author 梦举
    * @date 2023/4/6 21:04
    */
    public static void getFile(String bucket, String filepath, String outFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
            try(InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(filepath).build());
                FileOutputStream fileOutputStream = new FileOutputStream(new File(outFile));
            ) {
                // 从stream流中读取数据
                IOUtils.copy(stream, fileOutputStream);
                System.out.println("下载成功");
            }
        } catch (MinioException e) {
            System.out.println("Error occured: " + e);
            System.out.println("Htpp trace: " + e.httpTrace());
        }
    }


    public static void main(String[] args) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
         upload();
        // getFile("test", "告五人 - 唯一.mp3", "D:\\test\\告五人 - 唯一.mp3");
        // delete("test", "告五人 - 唯一.mp3");
    }

}