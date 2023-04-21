package com.meng.service;

import com.meng.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/3/22 10:44
 */

public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}