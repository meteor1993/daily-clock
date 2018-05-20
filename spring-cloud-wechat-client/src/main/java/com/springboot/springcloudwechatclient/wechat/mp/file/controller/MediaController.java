package com.springboot.springcloudwechatclient.wechat.mp.file.controller;

import com.springboot.springcloudwechatclient.system.model.AjaxJson;
import com.springboot.springcloudwechatclient.system.utils.DateUtils;
import com.springboot.springcloudwechatclient.wechat.mp.model.FileMeta;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by weishiyao on 2018/1/14.
 * 多媒体文件上传下载
 */
@RestController
@RequestMapping(value = "/mediaController")
public class MediaController {

    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

//    WxMpUtil wxMpUtil = ResourceWxMpUtil.getDefaultWxMpUtil();
    @Autowired
    private WxMpService wxMpService;
    /**
     * 媒体下载
     * @param request
     * @param fileMeta
     * @return
     */
    @RequestMapping(params = "ajaxDownMedia", method = RequestMethod.POST)
    public AjaxJson dowm(HttpServletRequest request, FileMeta fileMeta) {
        logger.info("===================mediaController.ajaxDownMedia");
        String media_id = request.getParameter("mediaId");//媒体id
        String pathPlx = request.getParameter("pathPlx") == null ? "":request.getParameter("pathPlx");//所属目录名
        String path ="upload/media/"+pathPlx+"/";

        AjaxJson ajaxJson = new AjaxJson();
        try {
            String realPath = request.getSession().getServletContext().getRealPath("/") + "/" + path ;// 文件的硬盘真实路径
            File realfileDir = new File(realPath);
            if(!realfileDir.exists()) {
                realfileDir.mkdir();
            }
            String savePath = realPath;// 文件保存目录
            String fileName = downMediaByMediaId(media_id, savePath);//下载文件并且返回文件路径
            fileMeta = new FileMeta();
            fileMeta.setSavePath(path+fileName);
            ajaxJson.setObj(fileMeta);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            ajaxJson.setSuccess(false);
            ajaxJson.setMsg("下载:"+media_id+"失败!");
        }
        return ajaxJson;
    }

    public String downMediaByMediaId(String mediaId,String savePath) throws Exception{
        logger.info("==================downMediaByMediaId.mediaId" + mediaId);
        File mediaFile = null;
        String fileName = "";
        mediaFile = wxMpService.getMaterialService().mediaDownload(mediaId);
        logger.info("==================downMediaByMediaId.mediaFile" + mediaFile);
        fileName = DateUtils.getDate("yyyyMMdd")+mediaFile.getName();
        savePath = savePath+"/"+fileName;
        logger.info("==================downMediaByMediaId.savePath" + savePath);
        FileCopyUtils.copy(mediaFile,new File(savePath));
        return fileName;
    }
}
