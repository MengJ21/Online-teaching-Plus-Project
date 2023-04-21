package com.meng;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.meng.media.service.MediaFileService;
import com.meng.media.service.impl.MediaFileServiceImpl;
import lombok.val;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * @author 梦举
 * @version 1.0
 * @description 大文件处理测试
 * @date 2023/4/9 13:36
 */
public class BigFileTest {

    // 测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("D:\\videos\\艺妓回忆录.mp4");
        String chunkPath = "D:\\videos\\chunk\\";
        // 分块存储地址。
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        //分块大小。
        long chunkSize = 1024 * 1024 * 1;
        // 根据分块大小计算分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数： " + chunkNum);
        // 缓冲区大小
        byte[] b = new byte[1024];
        // 使用RandomAccessFile访问文件。
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

        // 使用for循环来进行分块
        for (int i = 0;i < chunkNum;i++) {
            // 创建文件分块
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean  newFile = file.createNewFile();
            if (newFile) {
                // 向分块中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_write.close();
                System.out.println("完成分块" + i);
            }
        }
        raf_read.close();
    }
    // 测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        // 块文件目录
        File chunkFolder = new File("D:\\videos\\chunk");
        // 原始文件
        File originalFile = new File("D:\\videos\\艺妓回忆录.mp4");
        // 合并文件
        File mergeFile = new File("D:\\videos\\艺妓回忆录1.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        // 创建新的合并文件。
        mergeFile.createNewFile();
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        // 指针指向文件顶端。
        raf_write.seek(0);
        // 缓冲区
        byte[] b = new byte[1024];
        // 所有的分块文件。
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();

        // 校验文件
        try (FileInputStream fileInputStream = new FileInputStream(originalFile);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile)) {
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            String mergeMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }
        }
    }

    @Test
    public void getMd5() throws IOException {
        File file = new File("D:\\videos\\艺妓回忆录.mp4");
        InputStream inputStream = new FileInputStream(file);
        val s = DigestUtils.md5Hex(inputStream);
        System.out.println(s);
    }

    @Test
    public void testFileType() {
        String extension = "avi";
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (StringUtils.isNotEmpty(extension)) {
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        System.out.println(contentType);
    }

}