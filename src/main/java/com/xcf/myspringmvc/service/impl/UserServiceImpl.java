package com.xcf.myspringmvc.service.impl;

import com.xcf.myspringmvc.annotation.Qualifier;
import com.xcf.myspringmvc.annotation.Service;
import com.xcf.myspringmvc.dao.UserDao;
import com.xcf.myspringmvc.service.UserService;

/**
 * @author xuechaofu
 * @date 2018/12/27 19:13
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    @Qualifier("userDaoImpl")
    private UserDao userDao;
    @Override
    public void insert() {
        System.out.println("service 执行开始");
        userDao.insert();
        System.out.println("service 执行结束");
    }
}
