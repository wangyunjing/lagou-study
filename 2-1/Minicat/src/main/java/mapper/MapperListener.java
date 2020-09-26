package mapper;

import server.Context;
import server.Host;
import server.Service;

import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/9/27
 */
public class MapperListener {
    private Service service;

    public MapperListener(Service service) {
        this.service = service;
    }

    public void start() {
        List<Host> hosts = service.getHosts();
        hosts.forEach(this::registerHost);
    }

    private void registerHost(Host host) {
        Mapper mapper = service.getMapper();
        Mapper.MappedHost mappedHost = new Mapper.MappedHost(host.getName(), host);
        for (Context context : host.getContexts()) {
            Mapper.MappedContext mappedContext = new Mapper.MappedContext(context.getPrefix(), context);
            mappedHost.mapperContexts.add(mappedContext);
        }

        mapper.getMappedHosts().add(mappedHost);
    }
}
