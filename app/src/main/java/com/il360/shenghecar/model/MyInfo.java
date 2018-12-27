package com.il360.shenghecar.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/12/6 0006.
 */
public class MyInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int code;
    private String desc;
    private List<InfoCheck> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<InfoCheck> getResult() {
        return result;
    }

    public void setResult(List<InfoCheck> result) {
        this.result = result;
    }

}
