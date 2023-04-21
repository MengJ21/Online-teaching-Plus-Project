package com.meng.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 梦举
 * @version 1.0
 * @description 修改课程dto，只需要加一个主键。
 * @date 2023/3/23 16:42
 */

@Data
@ApiModel(value = "EditCourseDto", description = "修改课程基本信息主键")
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value = "课程名称", required = true)
    private Long id;
}