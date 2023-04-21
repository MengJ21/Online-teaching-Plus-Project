package com.meng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meng.dto.TeachplanDto;
import com.meng.po.Teachplan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /***
    * @description 查询课程的课程计划，组成树型结构。
    * @param courseId
    * @return java.util.List<com.meng.dto.TeachplanDto>
    * @author 梦举
    * @date 2023/3/25 10:49
    */
    public List<TeachplanDto> selectTreeNodes(long courseId);

}
