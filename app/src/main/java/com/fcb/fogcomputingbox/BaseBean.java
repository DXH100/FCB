package com.fcb.fogcomputingbox;

/**
 * author: denghx
 * date  : 2018/1/27
 * desc  :
 */

public class BaseBean {
    public String code;
    public String msg;
    public Object data;

    @Override
    public String toString() {
        return "BaseBean{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
