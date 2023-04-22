package com.nyj.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nyj.route.RouteCode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ControllerCommand implements Runnable{
    private Socket socket;
    public static JSONObject jsonObject = new JSONObject();
    public ControllerCommand(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {

            InputStream is = socket.getInputStream();
            char[] data = new char[1];
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int len = 0;
            String command = "";
            RouteCode rc = new RouteCode(socket);
            while ((len = br.read(data)) != -1) {
                String s = String.valueOf(data, 0, len);
                command += s;
                if("\n".equals(s)){
                    jsonObject = JSON.parseObject(command);
                    rc.routeCode(jsonObject);
                    command = "";
                }
            }
        }catch (Exception e) {
            System.out.println("前端控制下线啦！！！");
        }
    }
}
