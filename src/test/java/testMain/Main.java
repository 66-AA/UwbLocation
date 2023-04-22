package testMain;

import com.nyj.socket.MyServer;
import com.nyj.thread.BackendConsumeConcurrentHashMapThread;
import com.nyj.thread.BackendDataSolvingThread;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/18 10:20
 */
public class Main {
    public static void main(String[] args) {
        BackendDataSolvingThread backendDataSolvingThread = new BackendDataSolvingThread();
        MyServer.threadPool.execute(backendDataSolvingThread);
        MyServer.threadPool.execute(new BackendConsumeConcurrentHashMapThread());
        MyServer.MyTCPServer(31000);
    }
}
