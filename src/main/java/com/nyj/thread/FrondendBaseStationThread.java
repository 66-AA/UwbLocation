package com.nyj.thread;

import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/19 21:31
 * 前端得线程
 */
public class FrondendBaseStationThread implements Callable {
    private Socket socket;
    public FrondendBaseStationThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public Object call() throws Exception {
        return null;
    }
}
