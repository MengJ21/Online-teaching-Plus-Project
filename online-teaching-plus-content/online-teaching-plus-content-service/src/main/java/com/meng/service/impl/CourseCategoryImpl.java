package com.meng.service.impl;

import com.meng.dto.CourseCategoryTreeDto;
import com.meng.mapper.CourseCategoryMapper;
import com.meng.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/3/22 10:45
 */
@Service
public class CourseCategoryImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        // 查询数据库得到的课程分类
        List<CourseCategoryTreeDto> courseCategories = courseCategoryMapper.selectTreeNode(id);
        // 最终返回的列表
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        HashMap<String, CourseCategoryTreeDto> mapTemp = new HashMap<>();
        courseCategories.forEach(item->{
            mapTemp.put(item.getId(), item);
            // 只将根节点的下级节点放入list。
            if (item.getParentid().equals(id)) {
                categoryTreeDtos.add(item);
            }
            // 查询父节点。
            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
            // 判断父节点是否为空
            if (courseCategoryTreeDto != null) {
                // 判断父节点是否有孩子，没有新建列表，如果有，则向孩子列表中添加新的数据。
                if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return categoryTreeDtos;
    }
}