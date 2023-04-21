package com.meng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meng.model.po.MediaProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {
    /**
    * @description 根据分片参数获取待处理任务
    * @param shardTotal 分片总数
     * @param shardindex 分片序号
     * @param count 任务数
    * @return java.util.List<com.meng.model.po.MediaProcess>
    * @author 梦举
    * @date 2023/4/15 17:10
    */
    @Select("select t.* from media_process t where t.id % #{shardTotal} = #{shardindex} and t.status = '1' limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardindex") int shardindex, @Param("count") int count);
}
