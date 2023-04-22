package TCP_CS;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.jupiter.api.Test;
/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/5 18:08
 */

public class MyServer {
    @Test
    public void MyTCPServer() throws IOException {
        /*
         * 1.建立ServerSocket对象，在指定端口监听，等待连接
         * 2.建立Socket对象，通过serversocket.accept()方法接收客户端请求
         *
         *
         *
         */
        //1.在本机的31000端口监听，等待连接
        ServerSocket serverSocket = new ServerSocket(31000);
        System.out.println("服务器在31000端口开启监听，等待连接...");
        //2.调用serversocket.accept()方法接收客户端请求，并返回一个socket对象
            //当没有客户端连接31000端口时，程序会阻塞，持续等待连接
            //如果有客户端连接，则会返回socket对象，程序继续
        Socket socket = serverSocket.accept();
        System.out.println("客户端连接成功");
        System.out.println("socket" + socket.getClass());
        //3.通过socket.getInputStream()读取客户端写入到数据通道的数据，显示
        InputStream inputStream = socket.getInputStream();
        //4.IO读取
        byte[] buf = new byte[1024];
        int readLen = 0;
        while((readLen=inputStream.read(buf)) != -1){
            System.out.println(new String(buf,0,readLen));
        }
        //5.通过socket.getOutputStream()把数据写进数据通道。
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello,client".getBytes());
        socket.shutdownOutput();
        //6.关闭流
        inputStream.close();
        outputStream.close();
        socket.close();
        serverSocket.close();
    }
}
