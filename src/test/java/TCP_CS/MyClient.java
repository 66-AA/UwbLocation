package TCP_CS;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/5 18:47
 *
 * 客户端服务端互相以字节流的方式tcp通信
 */
public class MyClient {
    @Test
    public void MyTCPClient() throws IOException {
        //思路
        //1.连接服务端(ip,端口)
        Socket socket = new Socket(InetAddress.getLocalHost(), 31000);
        //2.得到和socket对象关联的输出流对象
        OutputStream outputStream = socket.getOutputStream();
        //3.把信息输入到数据通道
        outputStream.write("hello.server".getBytes());  //将字符串转化为字节数组
        socket.shutdownOutput();
        //读取服务端写进数据通道的数据
        byte[] bytes = new byte[1024];
        InputStream inputStream = socket.getInputStream();
        int readLen = 0;
        while ((readLen = inputStream.read(bytes)) != -1){
            System.out.println(new String(bytes,0,readLen));
        }
            //4.关闭流对象和socket，必须关闭
            outputStream.close();
        inputStream.close();
        socket.close();
        System.out.println("客户端退出");
    }
}
