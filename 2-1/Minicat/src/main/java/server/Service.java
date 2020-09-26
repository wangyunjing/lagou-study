package server;

import mapper.Mapper;
import mapper.MapperListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/9/26
 */
public class Service {

    private MapperListener mapperListener = new MapperListener(this);

    private Mapper mapper = new Mapper();

    private Connector connector;

    private List<Host> hosts = new ArrayList<>();

    public void start() {
        for (Host host : hosts) {
            host.start();
        }
        mapperListener.start();
        connector.start();
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}
