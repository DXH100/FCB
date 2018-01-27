package com.fcb.fogcomputingbox;

/**
 * Created by borui on 2017/10/18.
 */

public class BaseServerObj {

    //    public int StatusCode;
    public boolean IsSuccess;
    //10002
    public int ReasonCode;
    public String DataCount;
    /**
     *  JSONObject.toJavaObject
     */
    public String Data;
    public String Message;
    public String flag;
    public Object contentObj;

    @Override
    public String toString() {
        return "BaseServerObj{" +
                "IsSuccess='" + IsSuccess + '\'' +
                ", ReasonCode=" + ReasonCode +
                ", DataCount='" + DataCount + '\'' +
                ", Data=" + Data +
                ", Message=" + Message +
                ", flag=" + flag +
                ", contentObj=" + contentObj +
                '}';
    }
}
