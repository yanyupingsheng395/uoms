package com.linksteady.qywx.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.storage.impl.RedisConfigStorageImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/qywx")
public class QywxSettingController {

    @Autowired
    private QywxService qywxService;

    @Autowired
    private CommonFunService commonFunService;

    /**
     * 获取企业微信应用配置
     * @return
     */
    @GetMapping("/getQywxParam")
    public ResponseBo getQywxParam(){
        RedisConfigStorageImpl storage = qywxService.getRedisConfigStorage();
        String corpId = storage.getCorpId();
        String secret = storage.getSecret();
        String agentId=qywxService.getAgentId();
        Map<String,Object> result= Maps.newHashMap();
        if(StringUtils.isNotEmpty(corpId)&&StringUtils.isNotEmpty(secret)){
            result.put("corpId",corpId);
            result.put("secret",secret);
            result.put("agentId",agentId);
            result.put("msg","Y");
        }else {
            result.put("msg","N");
        }
        return ResponseBo.ok(result);
    }

    /**
     * 修改企业微信应用配置
     * @param corpId  企业微信ID
     * @param secret  应用秘钥
     * @return
     */
    @PostMapping("/updateCorpInfo")
    public ResponseBo updateCorpInfo(String corpId,String secret,String agentId){
        try {
            qywxService.updateCorpInfo(corpId, secret,agentId);
            return ResponseBo.ok();
        } catch (Exception e) {
            return ResponseBo.error();
        }
    }

    /**
     * 获取外部联系人信息
     * @return
     */
    @GetMapping("/getContact")
    public ResponseBo getContact(){
        String eventToken = qywxService.getEcEventToken();
        String eventAesKey = qywxService.getEcEventAesKey();
        SysInfoBo qywx = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
        String eventUrl="";
        if(qywx!=null){
            String domain = qywx.getSysDomain();
            if(StringUtils.isNotEmpty(domain)){
                eventUrl=domain+"/contractEvent/event";
            }
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("eventToken",eventToken);
        result.put("eventAesKey",eventAesKey);
        result.put("eventUrl",eventUrl);
        return ResponseBo.ok(result);
    }

    /**
     * 更新外部联系人信息
     * @return
     */
    @PostMapping("/updateContact")
    public ResponseBo updateContact(String eventToken,String eventAesKey){
        try {
            qywxService.updateContact(eventToken, eventAesKey);
            return ResponseBo.ok();
        }catch (Exception e){
            return ResponseBo.error();
        }
    }

    /**
     * 获取小程序ID
     */
    @GetMapping("/getAppID")
    public ResponseBo getAppID(){
        String appId = qywxService.getMpAppId();
        Map<String,Object> result= Maps.newHashMap();
        result.put("appId",appId);
        return ResponseBo.ok(result);
    }

    /**
     * 更新小程序ID
     */
    @PostMapping("/setMpAppId")
    public ResponseBo setMpAppId(String mpappid){
        try {
            qywxService.setMpAppId(mpappid);
            return ResponseBo.ok();
        }catch (Exception e){
            return ResponseBo.error();
        }
    }

    /**
     * 获取是否开启欢迎语
     */
    @GetMapping("/getEnableWel")
    public ResponseBo getEnableWel(){
        String status = qywxService.getEnableWelcome();
        Map<String,Object> result= Maps.newHashMap();
        result.put("status",status);
        return ResponseBo.ok(result);
    }

    /**
     * 设置欢迎语状态
     */
    @PostMapping("/setEnableWelcome")
    public ResponseBo setEnableWelcome(String status){
        try {
            qywxService.setEnableWelcome(status);
            return ResponseBo.ok();
        }catch (Exception e){
            return ResponseBo.error();
        }
    }

    /**
     *存储校验文件内容和名称
     */
    @PostMapping("/saveFile")
    public ResponseBo saveFile(String title,@RequestParam("file")  MultipartFile file){
        try {
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
            if(".txt".equals(fileType)){
                String filecount=analyseFile(multipartFileToFile(file));
                qywxService.saveFile(title,filecount);
                return ResponseBo.ok();
            }else{
                return ResponseBo.error("请上传txt文件！");
            }
        }catch (Exception e){
            return ResponseBo.error();
        }
    }

    /**
     *读取txt文件内容
     */
    private String analyseFile(File file){
        StringBuilder stringBuilder=new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
               stringBuilder.append(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 将MultipartFile转成file格式
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {
        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            String filePath = "file/";
            File dir = new File(filePath);
            if (!dir.exists() && !dir.isDirectory()) {
                dir.mkdirs();
            }
            ins = file.getInputStream();
            toFile = new File(filePath+file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    /**
     * 获取流文件
     */
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取校验文件内容和名称
     */
    @GetMapping("/getFileMessage")
    public ResponseBo getFileMessage(){
        QywxParam qywxParam=qywxService.getFileMessage();
        return ResponseBo.okWithData(null,qywxParam);
    }
}
