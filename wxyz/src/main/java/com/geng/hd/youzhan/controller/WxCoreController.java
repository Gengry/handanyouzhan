package com.geng.hd.youzhan.controller;

import com.geng.hd.youzhan.config.WxConfiguration;
import com.geng.hd.youzhan.encryption.AesException;
import com.geng.hd.youzhan.encryption.WXBizMsgCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Controller
public class WxCoreController {

    @RequestMapping(value = "/" , method = RequestMethod.GET)
    public void HandlerWx(HttpServletRequest request, HttpServletResponse response){
        // 微信加密签名
        String msg_signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        // 打印请求地址
        System.out.println("request=" + request.getRequestURL());
        // 流
        PrintWriter out = null;
        try{
            out = response.getWriter();
        }catch (Exception e){
            e.printStackTrace();
        }
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        String result = null;
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(WxConfiguration.TOKEN, WxConfiguration.EncodingAESKey, WxConfiguration.corpId);
            // 验证URL函数
            result = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);
        } catch (AesException e) {
            e.printStackTrace();
        }
        if (result == null) {
            // result为空，赋予token
            result = WxConfiguration.TOKEN;
        }
        // 拼接请求参数
        String str = msg_signature + " " + timestamp + " " + nonce + " " + echostr;
        // 打印参数+地址+result
        System.out.println("Exception:" + result + " " + request.getRequestURL() + " " + "FourParames:" + str);
        out.print(result);
        out.close();
        out = null;
    }

}
