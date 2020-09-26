package server;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.*;

/**
 * Minicat的主类
 */
public class Bootstrap {
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * Minicat启动需要初始化展开的一些操作
     */
    public void start() throws Exception {
        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );
        Service service = loadServer();
        service.start();
    }


    private Service loadServer() {
        Service service = new Service();
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            Element serviceElement = (Element) rootElement.selectSingleNode("//Service");

            Element connectorElement = (Element) serviceElement.selectSingleNode("Connector");
            Attribute port = connectorElement.attribute("port");
            Connector connector = new Connector(threadPoolExecutor);
            connector.setPort(Integer.valueOf(port.getValue()));
            connector.setService(service);
            service.setConnector(connector);

            List<Element> hostElementList = serviceElement.selectNodes("Host");
            for (Element hostElement : hostElementList) {
                Host host = new Host();
                Attribute name = hostElement.attribute("name");
                host.setName(name.getValue());
                Attribute appBase = hostElement.attribute("appBase");
                host.setAppBase(appBase.getValue());
                host.setService(service);
                service.getHosts().add(host);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }

    /**
     * Minicat 的程序启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
