package com.springboot.dailyclock.wxMpShare.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springboot.dailyclock.system.model.CommonJson;
import com.springboot.dailyclock.system.utils.Constant;
import com.springboot.dailyclock.wxMpShare.dao.WxMpShareDao;
import com.springboot.dailyclock.wxMpShare.model.WxMpShareModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/wxMpShare")
public class WxMpShareController {

    private final Logger logger = LoggerFactory.getLogger(WxMpShareController.class);

    @Autowired
    WxMpShareDao wxMpShareDao;

    /**
     * 根据unionID获取实体
     * @param unionId
     * @return
     */
    @PostMapping(value = "/getAllByUnionId")
    public CommonJson getAllByUnionId(@RequestParam String unionId) {
        logger.info("WxMpShareController.getAllByUnionId>>>>>>>>>>>>unionId:" + unionId);
        WxMpShareModel wxMpShareModel = wxMpShareDao.getAllByUnionId(unionId);
        CommonJson json = new CommonJson();
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        Map<String, Object> map = Maps.newHashMap();
        map.put("wxMpShareModel", wxMpShareModel);
        json.setResultData(map);
        return json;
    }

    /**
     * 根据preUnionId获取wxMpShareModelList
     * @param preUnionId
     * @return
     */
    @PostMapping(value = "/findAllByPreUnionId")
    public CommonJson findAllByPreUnionId(@RequestParam String preUnionId) {
        logger.info("WxMpShareController.findAllByPreUnionId>>>>>>>>>>>>>preUnionId:" + preUnionId);
        CommonJson json = new CommonJson();
        List<WxMpShareModel> wxMpShareModelList = wxMpShareDao.findAllByPreUnionId(preUnionId);
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        Map<String, Object> map = Maps.newHashMap();
        map.put("wxMpShareModelList", wxMpShareModelList);
        json.setResultData(map);
        return json;
    }

    /**
     * 保存或更新WxMpShareModel
     * @param wxMpShareModel
     * @return
     */
    @PostMapping(value = "/saveWxMpShareModel")
    public CommonJson findAllByPreUnionId(@RequestBody WxMpShareModel wxMpShareModel) {
        logger.info("WxMpShareController.findAllByPreUnionId>>>>>>>>>>>>>>WxMpShareModel:" + JSON.toJSONString(wxMpShareModel));
        WxMpShareModel wxMpShareModel1 = wxMpShareDao.save(wxMpShareModel);
        CommonJson json = new CommonJson();
        json.setResultCode(Constant.JSON_SUCCESS_CODE);
        json.setResultMsg("success");
        Map<String, Object> map = Maps.newHashMap();
        map.put("wxMpShareModel", wxMpShareModel1);
        json.setResultData(map);
        return json;
    }
}
