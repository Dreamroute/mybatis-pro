package com.github.dreamroute.mybatis.pro.sample.springboot.service.impl;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import com.github.dreamroute.mybatis.pro.sample.springboot.service.UserService;
import com.github.dreamroute.mybatis.pro.service.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author w.dehai
 */
@Service
public class UserServiceImpl extends AbstractServiceImpl<User, Long> implements UserService {
}
