package com.meng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 梦举
 * @version 1.0
 * @description 课程预览，发布
 * @date 2023/4/16 21:14
 */

@Controller
public class CoursePublishController {

    @GetMapping("/coursepreView/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", null);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }
}