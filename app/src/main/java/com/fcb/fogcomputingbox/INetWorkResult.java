package com.fcb.fogcomputingbox;

/**
 * Created by borui on 2017/12/20.
 */

public interface INetWorkResult {

    void error(int msgId, BaseServerObj serverObj);

    void success(int msgId, BaseServerObj serverObj);
}
