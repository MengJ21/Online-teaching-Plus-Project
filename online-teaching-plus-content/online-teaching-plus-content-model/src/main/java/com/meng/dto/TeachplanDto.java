package com.meng.dto;

import com.meng.po.Teachplan;
import com.meng.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/3/25 10:41
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {

    // 课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;

    // 子节点
    List<TeachplanDto> teachPlanTreeNodes;
}