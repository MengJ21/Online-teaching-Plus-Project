package com.meng.service;

import com.meng.dto.BindTeachplanMediaDto;
import com.meng.dto.SavaTeachPlanDto;
import com.meng.dto.TeachplanDto;
import com.meng.po.Teachplan;
import com.meng.po.TeachplanMedia;

import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description 课程基本信息管理业务接口
 * @date 2023/3/25 11:15
 */

public interface TeachPlanService {
    /***
    * @description 查询课程计划树形结构
    * @param courseId
    * @return java.util.List<com.meng.dto.TeachplanDto>
    * @author 梦举
    * @date 2023/3/25 11:17
    */
    public List<TeachplanDto> findTeachPlanTree(long courseId);

    /***
    * @description 修改课程计划
    * @param savaTeachPlanDto
    * @return void
    * @author 梦举
    * @date 2023/3/25 12:09
    */
    public void saveTeachPlan(SavaTeachPlanDto savaTeachPlanDto);
    /**
    * @description 为教学计划绑定媒资视频
    * @param bindTeachplanMediaDto 传递参数
    * @return com.meng.po.TeachplanMedia
    * @author 梦举
    * @date 2023/4/16 10:24
    */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}