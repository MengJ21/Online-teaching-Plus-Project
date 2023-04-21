package com.meng.dto;

import com.meng.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description 课程分类树型节点dto
 * @date 2023/3/20 22:01
 */

@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}