package com.xcf.myspringmvc.controller;

import com.xcf.myspringmvc.annotation.Controller;
import com.xcf.myspringmvc.annotation.Qualifier;
import com.xcf.myspringmvc.annotation.RequestMapping;
import com.xcf.myspringmvc.service.UserService;

/**
 * @author xuechaofu
 * @date 2018/12/27 17:27
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {
    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping("/insert")
    public void insert(){
        System.out.println("--------controller 执行开始");
        userService.insert();
        System.out.println("--------controller 执行结束");
    }
}
