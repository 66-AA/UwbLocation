package com.nyj.receive;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import com.alibaba.fastjson.JSONObject;
import com.nyj.thread.BackendConsumeConcurrentHashMapThread;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/22 10:16
 */
public class ReceiveData {
    public void receivePointsData(JSONObject jsonObject, Socket socket) {
        jsonObject.put("data", BackendConsumeConcurrentHashMapThread.pointConcurrentHashMap);
        ArrayList<String> tagIds = new ArrayList<>();
        BackendConsumeConcurrentHashMapThread.pointConcurrentHashMap.forEach((String key, Object value) -> {
            tagIds.add(key);
        });
        jsonObject.put("tagId",tagIds);
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
            os.write(jsonObject.toJSONString().getBytes());
            jsonObject = JSONObject.parseObject("{\"code\":10001,\"data\":{},\"tagId\":{}}");
            os.flush();
            BackendConsumeConcurrentHashMapThread.pointConcurrentHashMap.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
