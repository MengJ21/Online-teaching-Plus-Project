package com.meng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.meng.dto.CourseCategoryTreeDto;
import com.meng.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    public List<CourseCategoryTreeDto> selectTreeNode(String id);
    public List<CourseCategory> findAll1();
}
