package com.meng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/4/16 18:42
 */

@Controller
public class FreemarkerController {

    @GetMapping("/testfreemarker")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "小明");
        // 设置数据模型。
        modelAndView.setViewName("test");
        // 设置模板名称
        return modelAndView;
    }

}