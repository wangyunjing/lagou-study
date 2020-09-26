package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yunjing.wang
 * @date 2020/9/26
 */
public class Connector {
    ThreadPoolExecutor threadPoolExecutor;
    private int port;
    private Service service;

    public Connector(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                RequestProcessor requestProcessor = new RequestProcessor(socket, service.getMapper());
                threadPoolExecutor.execute(requestProcessor);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
