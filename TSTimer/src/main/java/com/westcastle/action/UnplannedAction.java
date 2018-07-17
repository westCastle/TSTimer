package com.westcastle.action;

import com.westcastle.config.R;
import com.westcastle.core.ActionContext;
import com.westcastle.database.BaseDao;
import net.sf.json.JSONObject;

import java.util.Map;

public class UnplannedAction {

    public void list(){
        String TEL = ActionContext.getString("TEL");
        String whereStr = String.format("TEL = '%n", TEL);
        Map<String,Object> unplan = BaseDao.getOneByWhere(
                R.tables.unplanned,whereStr);
        ActionContext.setMap("unplaned",unplan);
    }

    public void add(){
        JSONObject obj = ActionContext.getInstance().getRequestData();
        Map<String, Object> unplan = BaseDao.insert(R.tables.unplanned, obj);
        ActionContext.setMap("unplaned", unplan);
    }
}
