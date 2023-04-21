package com.meng.media.service.impl;

import com.meng.media.mapper.MediaProcessMapper;
import com.meng.media.service.MediaFileProcessService;
import com.meng.model.po.MediaProcess;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/4/15 17:28
 */
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {
    @Resource
    MediaProcessMapper mediaProcessMapper;
    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        List<MediaProcess> mediaProcesses = mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
        return mediaProcesses;
    }

}