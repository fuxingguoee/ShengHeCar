package com.il360.shenghecar.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/11/29 0029.
 */

public class Branch implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int code;
    private String desc;
    private List<Company> result;

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

    public List<Company> getResult() {
        return result;
    }

    public void setResult(List<Company> result) {
        this.result = result;
    }

}
