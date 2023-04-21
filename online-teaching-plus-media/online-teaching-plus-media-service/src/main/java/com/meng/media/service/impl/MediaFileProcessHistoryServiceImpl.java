package com.meng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.meng.media.mapper.MediaFilesMapper;
import com.meng.media.mapper.MediaProcessHistoryMapper;
import com.meng.media.mapper.MediaProcessMapper;
import com.meng.media.service.MediaFileProcessHistoryService;
import com.meng.media.service.MediaFileProcessService;
import com.meng.model.po.MediaFiles;
import com.meng.model.po.MediaProcess;
import com.meng.model.po.MediaProcessHistory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.print.attribute.standard.Media;
import java.time.LocalDateTime;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/4/15 17:35
 */
@Service
public class MediaFileProcessHistoryServiceImpl implements MediaFileProcessHistoryService {

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Resource
    MediaProcessHistoryMapper mediaProcessHistoryMapper;
    @Override
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errMsg) {
        // 查出任务, 如果不存在则直接返回。
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (mediaProcess == null) {
            return ;
        }
        // 处理失败，更新任务处理结果。
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        if (status.equals("3")) {
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrorMsg(errMsg);
            mediaProcessMapper.update(mediaProcess_u, queryWrapperById);
            return;
        }
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles != null) {
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }
        // 处理成功，更新url和状态
        mediaProcess.setUrl(url);
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);
        // 添加到历史记录
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        // 删除mediaProcess
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }
}