package net.generic.ipc;

import net.generic.ipc.IResponse;
import net.generic.ipc.model.RequestData;

interface IRequest {

    String getSchema();

    void subscribe(String pkg, String config);

    List<String> requestData(String pkg,in List<RequestData> dataRequest);

    void registerCallBack(String pkg, IResponse response);

    void unregisterCallBack(String pkg);
}