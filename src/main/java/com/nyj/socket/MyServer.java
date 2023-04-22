package com.nyj.socket;

import com.nyj.thread.BackendReceiveBaseStationThread;
import com.nyj.thread.FrondendBaseStationThread;
import com.nyj.thread.ThreadPool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/5 20:59
 * 如果通过状态机接收到数据正确，就把存储在fieldDataDefinition的字段信息存储到ConcurrentLinkedQueue中，
 * <p>
 * ConcurrentLinkedQueue是Java中的一个线程安全的队列，它实现了Queue接口，
 * 并且内部使用链表实现。它支持高并发访问，而且在并发环境下不需要加锁就可以保证线程安全。
 * ConcurrentLinkedQueue中的元素遵循先进先出（FIFO）的原则。它提供了一些基本的队列操作方法，
 * 例如offer()用于添加元素、poll()用于获取并移除队列头部的元素、peek()用于获取但不移除队列头部的元素等等。
 * <p>
 * 由于ConcurrentLinkedQueue是无界队列，因此它不会阻塞生产者线程，即使队列已经达到最大容量，
 * 也只会返回false。这一点与有界队列（例如ArrayBlockingQueue）不同。
 * <p>
 * ConcurrentLinkedQueue适用于多个线程并发读写的场景，它的性能通常比使用锁机制实现的队列更好。
 * 同时，它还可以作为一种数据缓存结构，用于异步处理和消息传递等场景。
 */
public class MyServer {
    public static ThreadPoolExecutor threadPool = new ThreadPool().createThreadPoolExector();  //创建一个线程池
    private static ArrayList<String> anchorIPList = new ArrayList<String>();   //存放基站IP

    public static  void MyTCPServer(Integer port) {

        Socket socket = null;
        try {
            anchorIPList.add("192.168.1.251");
            anchorIPList.add("192.168.1.252");
            anchorIPList.add("192.168.1.253");
            anchorIPList.add("192.168.1.254");
            socket = null;
            ServerSocket serverSocket = null;
            serverSocket = new ServerSocket(31000);
            System.out.println("服务端等待连接...");
            while (true) {
                socket = serverSocket.accept();  //阻塞等待客户端连接
                SocketAddress remoteSocketAddress = socket.getLocalSocketAddress(); //获取本机ip和端口号
                String hostAddress = socket.getInetAddress().getHostAddress();//获取客户端IP

                int port1 = socket.getPort(); //获取客户端端口号
                System.out.println("客户端连接成功...\n" + "客户端IP是：" + hostAddress + "\n" + "客户端端口号是：" + port1);
                if (anchorIPList.contains(hostAddress)) {
                    threadPool.execute(new BackendReceiveBaseStationThread(socket));
                } else {

                    threadPool.submit(new FrondendBaseStationThread(socket));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //打印异常的堆栈跟踪信息
            System.out.println(socket.getRemoteSocketAddress() + "已下线");
        }
    }

}