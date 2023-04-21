package com.meng.dto;

import lombok.Data;
import lombok.ToString;
import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/3/9 14:44
 */
@Data
@ToString
public class QueryCourseParamsDto {
    // 审核状态
    private String auditStatus;
    // 课程名称
    private String courseName;
    // 发布状态
    private String publishStatus;
}