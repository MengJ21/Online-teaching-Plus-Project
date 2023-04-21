package com.meng;

import com.meng.utils.Mp4VideoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OnlineTeachingPlusBaseApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        // ffmpeg路径
        String ffmpeg_path = "D:\\FFmpeg\\ffmpeg\\ffmpeg\\ffmpeg.exe";
        // 源avi视频的路径
        String video_path = "D:\\videos\\艺妓回忆录.avi";
        // 转换后的MP4路径
        String mp4_name = "艺妓回忆录1.mp4";
        // 转换后的MP4路径
        String mp4_path = "D:\\videos\\艺妓回忆录1.mp4";
        // 创建工具类对象
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_path);
        // 开始视频转换
        String s = videoUtil.generateMp4();
        System.out.println(s);
    }
}
