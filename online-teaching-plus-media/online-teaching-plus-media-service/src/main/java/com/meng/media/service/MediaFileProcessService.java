package com.meng.media.service;

import com.meng.model.po.MediaProcess;

import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description 媒资文件处理业务
 * @date 2023/4/15 17:27
 */

public interface MediaFileProcessService {
    /**
     * @description 获取待处理任务
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count 获取记录数
     * @return java.util.List<com.meng.model.po.MediaProcess>
     * @author 梦举
     * @date 2023/4/15 17:13
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

}