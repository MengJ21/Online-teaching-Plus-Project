package com.meng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.meng.dto.BindTeachplanMediaDto;
import com.meng.dto.SavaTeachPlanDto;
import com.meng.dto.TeachplanDto;
import com.meng.exception.OnlineTeachingPlusException;
import com.meng.mapper.TeachplanMapper;
import com.meng.mapper.TeachplanMediaMapper;
import com.meng.po.Teachplan;
import com.meng.po.TeachplanMedia;
import com.meng.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/3/25 11:18
 */
@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachPlanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Resource
    TeachplanMediaMapper teachplanMediaMapper;


    @Override
    public List<TeachplanDto> findTeachPlanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachPlan(SavaTeachPlanDto savaTeachPlanDto) {
        // 先获取课程计划id
        Long id = savaTeachPlanDto.getId();
        // 如果id存在就是修改
        if (id != null) {
            // 获取原本的计划信息
            Teachplan teachplan = teachplanMapper.selectById(id);
            // 将修改后的信息复制进去
            BeanUtils.copyProperties(savaTeachPlanDto, teachplan);
            // 保存。
            teachplanMapper.updateById(teachplan);
        } else {
            // id不存在，新增
            // 首先取出同父级别的课程计划数量。
            int count = getTeachPlanCount(savaTeachPlanDto.getCourseId(), savaTeachPlanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(savaTeachPlanDto, teachplanNew);
            teachplanMapper.insert(teachplanNew);
        }
    }

    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        // 教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if (teachplan == null) {
            OnlineTeachingPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if (grade != 2) {
            OnlineTeachingPlusException.cast("只允许二级计划绑定媒资文件");
        }
        // 课程id
        Long courseId = teachplan.getCourseId();
        // 先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));
        // 再添加教学嘉华与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    private int getTeachPlanCount(long courseId, long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }
}