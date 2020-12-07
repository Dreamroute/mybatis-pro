package com.github.dreamroute.mybatis.pro.sample.springboot.service.impl;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Dict;
import com.github.dreamroute.mybatis.pro.sample.springboot.service.DictService;
import com.github.dreamroute.mybatis.pro.service.service.AbstractServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * @author w.dehai
 *
 */
@Service
public class DictServiceImpl extends AbstractServiceImpl<Dict, Long> implements DictService {}
