package com.meng.service;

import com.meng.dto.AddCourseDto;
import com.meng.dto.CourseBaseInfoDto;
import com.meng.dto.EditCourseDto;
import com.meng.dto.QueryCourseParamsDto;
import com.meng.model.PageParams;
import com.meng.model.PageResult;
import com.meng.po.CourseBase;
import com.meng.po.CourseMarket;

/**
 * @author 梦举
 * @version 1.0
 * @description 课程基本信息管理业务接口
 * @date 2023/3/9 20:44
 */

public interface CourseBaseInfoService {
    /**
    * @description 查询所有课程
    * @param pageParams 分页参数
     * @param queryCourseParamsDto 条件参数
    * @return com.meng.model.PageResult<com.meng.po.CourseBase>
    * @author 梦举
    * @date 2023/3/9 20:52
    */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);
    /**
    * @description 添加课程信息
    * @param companyId 教学机构id
     * @param addCourseDto 课程基本信息
    * @return com.meng.dto.CourseBaseInfoDto
    * @author 梦举
    * @date 2023/3/22 21:01
    */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);
    /***
    * @description 根据课程id查询课程详细信息
    * @param courseID
    * @return com.meng.dto.CourseBaseInfoDto
    * @author 梦举
    * @date 2023/3/23 16:19
    */
    public CourseBaseInfoDto getCourseBaseInfo(long courseID);

    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

}