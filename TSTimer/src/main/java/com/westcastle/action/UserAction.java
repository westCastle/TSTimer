package com.westcastle.action;

import com.westcastle.config.R;
import com.westcastle.core.ActionContext;
import com.westcastle.database.BaseDao;
import com.westcastle.utils.SMSCodeUtils;
import net.sf.json.JSONObject;
import java.util.Map;

public class UserAction {

    public void login(){
        String TEL = ActionContext.getString("TEL");
        String pwd = ActionContext.getString("Password");
        System.out.println(TEL+pwd);

        String whereStr = String.format("TEL = 'n%' and pwd = 's%'", TEL, pwd);
        Map<String,Object> user = BaseDao.getOneByWhere(R.tables.user, whereStr);

        if(user != null)
        {
            ActionContext.setBool("success", true);
            user.remove("pwd");
            ActionContext.setMap("user", user);
        }
        else
        {
            ActionContext.setBool("登录成功", true);
        }
    }

//    public void logout(){
//
//    }

    public void getSMSCode(){

        String phone = ActionContext.getString("TEL");
        SMSCodeUtils.getSMSCode(phone);
        JSONObject obj = ActionContext.getInstance().getRequestData();
        Map<String, Object> user = BaseDao.insert(R.tables.user, obj);
        ActionContext.setMap("发送成功", user);
    }

    public void register(){

        getSMSCode();
        String SMSCode = ActionContext.getString("SMSCode");
        JSONObject smscode = ActionContext.getInstance().getRequestData();

        if(SMSCode != smscode.getString(String.valueOf(smscode)))
            System.out.println("验证码错误！");

        JSONObject obj = ActionContext.getInstance().getRequestData();
        Map<String, Object> user = BaseDao.insert(R.tables.user, obj);
        ActionContext.setMap("登录成功", user);

    }



    public void updatePassword(){

        getSMSCode();
        String SMSCode = ActionContext.getString("SMSCode");
        JSONObject smscode = ActionContext.getInstance().getRequestData();

        if(SMSCode != smscode.getString(String.valueOf(smscode)))
            System.out.println("验证码错误！");

        JSONObject obj = ActionContext.getInstance().getRequestData();
        Map<String, Object> user = BaseDao.update(R.tables.user, obj);
        ActionContext.setMap("修改密码成功", user);
    }
}
