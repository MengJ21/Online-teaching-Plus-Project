package com.meng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.meng.dto.AddCourseDto;
import com.meng.dto.CourseBaseInfoDto;
import com.meng.dto.EditCourseDto;
import com.meng.dto.QueryCourseParamsDto;
import com.meng.exception.OnlineTeachingPlusException;
import com.meng.mapper.CourseBaseMapper;
import com.meng.mapper.CourseCategoryMapper;
import com.meng.mapper.CourseMarketMapper;
import com.meng.model.PageParams;
import com.meng.model.PageResult;
import com.meng.po.CourseBase;
import com.meng.po.CourseCategory;
import com.meng.po.CourseMarket;
import com.meng.service.CourseBaseInfoService;
import com.meng.service.CourseMarketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 梦举
 * @version 1.0
 * @description 课程信息管理业务接口实现类
 * @date 2023/3/9 20:54
 */
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseMarketServiceImpl courseMarketServiceImpl;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 构建条件查询对象。
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 构建查询条件，根据课程名称查询。
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName,queryCourseParamsDto.getCourseName());
        // 构建查询条件，根据课程审核条件查询。
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        // 构建查询条件，根据课程发布状态查询。
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getPublishStatus());

        //分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据获取结果。
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<CourseBase> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        // 合法性校验
        if (StringUtils.isBlank(addCourseDto.getName())) {
            throw new OnlineTeachingPlusException("课程名称为空");
        }
        if (StringUtils.isBlank(addCourseDto.getMt())) {
            throw new OnlineTeachingPlusException("课程分类为空");
        }
        if (StringUtils.isBlank(addCourseDto.getSt())) {
            throw new OnlineTeachingPlusException("课程分类为空");
        }
        if (StringUtils.isBlank(addCourseDto.getGrade())) {
            throw new OnlineTeachingPlusException("课程等级为空");
        }
        if (StringUtils.isBlank(addCourseDto.getTeachmode())) {
            throw new OnlineTeachingPlusException("教育模式为空");
        }
        if (StringUtils.isBlank(addCourseDto.getUsers())) {
            throw new OnlineTeachingPlusException("适应人群为空");
        }
        if (StringUtils.isBlank(addCourseDto.getCharge())) {
            throw new OnlineTeachingPlusException("收费规则为空");
        }
        // 新增对象
        CourseBase courseBaseNew = new CourseBase();
        // 将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(addCourseDto, courseBaseNew);
        // 设置审核状态
        courseBaseNew.setAuditStatus("202002");
        // 设置发布状态
        courseBaseNew.setStatus("203001");
        // 机构id
        courseBaseNew.setCompanyId(companyId);
        // 添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程信息基本表。
        int insert = courseBaseMapper.insert(courseBaseNew);
        Long courseId = courseBaseNew.getId();
        // 课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarketNew);
        courseMarketNew.setId(courseId);
        // 收费规则
        String charge = addCourseDto.getCharge();

        //收费而课程必须写价格且价格大于0
        if (charge.equals("201001")) {
            BigDecimal price = addCourseDto.getPrice();
            if (price == null || price.floatValue() <= 0) {
                throw new RuntimeException("课程设置了收费价格不能为空且必须大于0");
            }
        }

        // 插入课程营销信息
        int insert1 = courseMarketMapper.insert(courseMarketNew);
        if (insert1 <= 0 || insert <= 0) {
            throw new RuntimeException("新增课程基本信息失败");
        }
        // 添加成功
        // 返回添加的课程信息
        return getCourseBaseInfo(courseId);
    }

    @Override
    public CourseBaseInfoDto getCourseBaseInfo(long courseID) {
        CourseBase courseBase = courseBaseMapper.selectById(courseID);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseID);

        if (courseBase == null) {
            return null;
        }
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);

        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        // 查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        Long courseId = editCourseDto.getId();
        CourseBase courseBaseUpdate = courseBaseMapper.selectById(courseId);
        if (!companyId.equals((courseBaseUpdate.getCompanyId()))) {
            OnlineTeachingPlusException.cast("只允许修改本机构的课程");
        }
        BeanUtils.copyProperties(editCourseDto, courseBaseUpdate);

        // 更新
        courseBaseUpdate.setChangeDate(LocalDateTime.now());
        courseBaseMapper.updateById(courseBaseUpdate);

        // 查询营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        if (courseMarket == null) {
            courseMarket = new CourseMarket();
        }

        courseMarket.setId(courseId);
        courseMarket.setCharge(editCourseDto.getCharge());

        // 收费规则
        String charge = editCourseDto.getCharge();

        // 收费课程必须写价格

        if (charge.equals("201001")) {
            BigDecimal price = editCourseDto.getPrice();
            if (price == null || price.floatValue() <= 0) {
                OnlineTeachingPlusException.cast("课程设置了收费价格不能为空且必须大于0");
            }
        }
        // 将dto中的课程营销信息拷贝至courseMarket中。
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        // 保存课程营销信息，没有则添加有则更新.
        boolean save = courseMarketServiceImpl.saveOrUpdate(courseMarket);

        return getCourseBaseInfo(courseId);

    }

}