package com.springboot.dailyclock.admin.controller;

import com.springboot.dailyclock.admin.dao.AdminInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by weisy on 2018/5/22
 */
@RestController
@RequestMapping(path = "/api")
public class AdminInfoController {

    private static final Logger logger = LoggerFactory.getLogger(AdminInfoController.class);

    @Autowired
    AdminInfoDao adminInfoDao;


}
