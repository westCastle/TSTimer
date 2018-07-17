package com.westcastle.utils;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 *
 * 获取短信验证码
 *
 * @author: westCastle
 * @date: 2018/7/17 9:15
 */
public class SMSCodeUtils {

    public static void getSMSCode(String telphoneString) {

        SMSCodeUtils smsUtils = new SMSCodeUtils();
        try {
            smsUtils.sendSms(telphoneString);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
    //随机验证码
    private int code;
    public int getCode(){
        return code;
    }
    public void setCode(){
        code = (int)(Math.random()*9999)+1000;  //每次调用生成一次四位数的随机数
    }
    //短信API产品名称
    static final String product="Dysmsapi";
    //短信API产品域名
    static final String domain="dysmsapi.aliyuncs.com";

    static final String accessKeyId = "LTAIMN7i3dYeY0FL";
    static final String accessKeySecret = "SzzsDAp20FgaC7V8PXH24wZxPHZo4R";

    public SendSmsResponse sendSms(String telphone) throws ClientException{
        //设置超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient
        IClientProfile profile=DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou",product, domain);
        IAcsClient acsClient=new DefaultAcsClient(profile);

        //组装请求对象
        SendSmsRequest request=new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //待发送的手机号
        request.setPhoneNumbers(telphone);
        //短信签名
        request.setSignName("westCastle");
        //短信模板ID
        request.setTemplateCode("SMS_139785217");
        //验证码
        SMSCodeUtils sms = new SMSCodeUtils();
        sms.setCode();
        String codetemp = sms.getCode()+"";
        System.out.println("code:        "+codetemp);

        SendSmsResponse response=acsClient.getAcsResponse(request);

        if(response.getCode() != null && response.getCode().equals("OK")) {
            //请求成功
            System.out.println("发送成功！");
            }else {
            System.out.println("发送失败！");
            }
        return response;
        }
}