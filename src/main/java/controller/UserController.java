package com.ityouse.controller;

import com.ityouse.model.User;
import com.ityouse.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 *@ClassName: UserController
 *@description: 类描述
 *@author: wangjunhui@wondersgroup.com
 *@data: 2018/5/11 14:59
 **/
@Controller
@RequestMapping("/")
public class UserController {
    @Resource
    private UserService userService;

    @ResponseBody
    public String toIndex(){
        User users = this.userService.getAll();
        return  users.toString();
    }
}
