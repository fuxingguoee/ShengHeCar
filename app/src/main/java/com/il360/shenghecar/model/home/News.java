package com.il360.shenghecar.model.home;

import java.io.Serializable;

/**
 * Created by lepc on 2018/8/1.
 */

public class News implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String pushId;
    private String title;
    private String context;
    private String sendTime;
    private Boolean isCheck;

    public Boolean isCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
