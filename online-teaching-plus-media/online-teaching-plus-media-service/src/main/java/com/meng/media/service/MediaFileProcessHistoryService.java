package com.meng.media.service;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/4/15 17:34
 */

public interface MediaFileProcessHistoryService {
    /**
     * @description 保存任务结果
     * @param taskId 任务id
     * @param status 任务状态
     * @param fileId 文件id
     * @param url url
     * @param errMsg 错误信息
     * @return void
     * @author 梦举
     * @date 2023/4/15 17:22
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errMsg);
}