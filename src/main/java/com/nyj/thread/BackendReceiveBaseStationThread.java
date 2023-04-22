package com.nyj.thread;

import com.nyj.fielddatadefinition.FieldDataDefinition;
import com.nyj.statemachineparsesbytebybyte.StateMachineParsesBytebybyte;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/19 21:23
 *
 */
public class BackendReceiveBaseStationThread implements Runnable{
    public static ConcurrentLinkedQueue<FieldDataDefinition> uwbReceiveInfoData = new ConcurrentLinkedQueue<>();  //存放解析过的四个基站的所有信息
    private Socket socket;
    public BackendReceiveBaseStationThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
        InputStream inputStream = socket.getInputStream();
        int readByte;
        StateMachineParsesBytebybyte stateMachineParsesBytebybyte = new StateMachineParsesBytebybyte();  //创建解析数据对象
        FieldDataDefinition fieldDataDefinition = null; //Uwb接受到的信息
        while ((readByte = inputStream.read())!=-1){  // 逐字节接收数据
            /*
             *1.String类提供了一个format()方法可以将任何类型的数据格式化为指定字符串格式
             *  使用"%02X"格式来将一个byte类型的数据转换为两位16进制数，其中X表示大写的十六进制数字。
             *2.
             * */
            /*String format = String.format("%02X", readByte);
            System.out.println(format);*/
            String s = Integer.toHexString(readByte & 0XFF).toUpperCase();
            if(s.length()==1){
                s = "0" + s;
            }
            //System.out.println("s" + s);
            fieldDataDefinition = stateMachineParsesBytebybyte.parsingData(readByte);
            if(fieldDataDefinition != null){
                uwbReceiveInfoData.offer(fieldDataDefinition); //如果状态机拿到的数据正确，就把这组数据添加到链表队列中
                fieldDataDefinition = null;
            }
        }
    } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
