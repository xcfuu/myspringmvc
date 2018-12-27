package com.xcf.myspringmvc.dao.impl;

import com.xcf.myspringmvc.annotation.Repository;
import com.xcf.myspringmvc.dao.UserDao;

/**
 * @author xuechaofu
 * @date 2018/12/27 19:17
 */
@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao {
    @Override
    public void insert() {
        System.out.println("---------dao   执行");
    }
}
