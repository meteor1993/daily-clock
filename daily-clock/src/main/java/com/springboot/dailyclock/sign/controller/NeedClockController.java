package com.springboot.dailyclock.sign.controller;

import com.google.common.collect.Maps;
import com.springboot.dailyclock.sign.dao.NeedClockUserDao;
import com.springboot.dailyclock.sign.model.NeedClockUserModel;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/needClock")
public class NeedClockController {

    private final Logger logger = LoggerFactory.getLogger(NeedClockController.class);

    @Autowired
    NeedClockUserDao needClockUserDao;

    @PostMapping(value = "/getByOpenidAndNeedDate")
    public CommonJson getByOpenidAndNeedDate(@RequestParam String openid, @RequestParam String needDate) {
        CommonJson json = new CommonJson();
        NeedClockUserModel needClockUserModel = needClockUserDao.getByOpenidAndNeedDate(openid, needDate);
        Map<String, Object> map = Maps.newHashMap();
        map.put("needClockUserModel", needClockUserModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }

    @PostMapping(value = "/deleteNeedClock")
    public CommonJson deleteNeedClock(@RequestBody NeedClockUserModel needClockUserModel) {
        CommonJson json = new CommonJson();
        needClockUserDao.delete(needClockUserModel);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        return json;
    }

    @PostMapping(value = "/saveNeedClock")
    public CommonJson saveNeedClock(@RequestBody NeedClockUserModel needClockUserModel) {
        CommonJson json = new CommonJson();
        NeedClockUserModel needClockUserModel1 = needClockUserDao.save(needClockUserModel);

        Map<String, Object> map = Maps.newHashMap();
        map.put("needClockUserModel", needClockUserModel1);

        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        json.setResultData(map);
        return json;
    }
}
