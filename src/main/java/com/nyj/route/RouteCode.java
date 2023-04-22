package com.nyj.route;

/*
* 连接前端的时候使用
*
* */
import com.alibaba.fastjson.JSONObject;
import com.nyj.receive.ReceiveData;


import java.net.Socket;

public class RouteCode {
    private Socket socket;

    public RouteCode(Socket socket) {
        this.socket = socket;
    }

    public void routeCode(JSONObject jsonObject){
        String code = jsonObject.get("code").toString();
        ReceiveData receiveData = new ReceiveData();
        switch (code) {
            case "10001":
              receiveData.receivePointsData(jsonObject,socket);
            break;

        }
    }
}
