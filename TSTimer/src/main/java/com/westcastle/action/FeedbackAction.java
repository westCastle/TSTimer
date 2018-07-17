package com.westcastle.action;

import com.westcastle.config.R;
import com.westcastle.core.ActionContext;
import com.westcastle.database.BaseDao;
import net.sf.json.JSONObject;

import java.util.Map;

public class FeedbackAction {

    public void list(){
        String TEL = ActionContext.getString("TEL");
        String whereStr = String.format("TEL = '%n", TEL);
        Map<String,Object> feedback = BaseDao.getOneByWhere(
                R.tables.feedback,whereStr);
        ActionContext.setMap("feedback",feedback);

    }

    public void add(){
        JSONObject obj = ActionContext.getInstance().getRequestData();
        Map<String, Object> feedback = BaseDao.insert(R.tables.feedback, obj);
        ActionContext.setMap("feedback", feedback);
    }
}
