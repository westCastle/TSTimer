package com.westcastle.action;

import com.westcastle.config.R;
import com.westcastle.core.ActionContext;
import com.westcastle.database.BaseDao;
import net.sf.json.JSONObject;

import java.util.Map;

public class PlanAction {


    public void list(){
        String UserID = ActionContext.getString("UserID");
        //String entityName = ActionContext.getString("entityName");
        String whereStr = String.format("UserID = s%", UserID);
        Map<String,Object> plan = BaseDao.getOneByWhere(
                R.tables.plan,whereStr);
        ActionContext.setMap("plan",plan);
    }

    public void getDetail(){
        String UserID = ActionContext.getString("UserID");
        String Abstract = ActionContext.getString("Abstract");
        //String entityName = ActionContext.getString("entityName");
        String whereStr = String.format("UserID = s% and Abstract = s%", UserID, Abstract);
        Map<String,Object> plan = BaseDao.getOneByWhere(
                R.tables.plan,whereStr);
        ActionContext.setMap("plan",plan);
    }

    public void add(){
        JSONObject obj = ActionContext.getInstance().getRequestData();
        Map<String, Object> plan = BaseDao.insert(R.tables.plan, obj);
        ActionContext.setMap("plan", plan);
    }
}
