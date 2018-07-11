package com.springcloud.gatherdata.admin.controller;


import com.springcloud.gatherdata.admin.service.AdminService;
import com.springcloud.gatherdata.system.model.CommonJson;
import com.springcloud.gatherdata.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * Created by weisy on 2018/5/22
 */
@RestController
@RequestMapping(path = "/admin")
public class AdminInfoController {

    private final Logger logger = LoggerFactory.getLogger(AdminInfoController.class);

    @Autowired
    AdminService adminService;

    /**
     * 每日数据结算
     * @return
     */
    @PostMapping(value = "/gatherData")
    public CommonJson gatherData() {
        CommonJson json = new CommonJson();
        try {
            json = adminService.gatherData();
            json.setResultCode(Constant.JSON_SUCCESS_CODE);
            json.setResultMsg("success");
        } catch (ParseException e) {
            json.setResultCode(Constant.JSON_ERROR_CODE);
            json.setResultMsg("fail");
        }
        return json;
    }

}
