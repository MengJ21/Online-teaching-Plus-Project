package com.meng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meng.po.CourseBase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程基本信息 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseBaseMapper extends BaseMapper<CourseBase> {
    List<CourseBase> getAll1();
}
