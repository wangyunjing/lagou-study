package mapper;

import server.Context;
import server.Host;
import server.HttpServlet;
import server.StaticHttpServlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yunjing.wang
 * @date 2020/9/26
 */
public class Mapper {

    private List<MappedHost> mappedHosts = new ArrayList<>();

    public void map(String host, String url, MappingData mappingData) {
        for (MappedHost mappedHost : mappedHosts) {
            if (host.startsWith(mappedHost.name)) {
                mappingData.setMappedHost(mappedHost);
                break;
            }
        }
        MappedHost mappedHost = mappingData.getMappedHost();
        if (mappedHost == null) {
            return;
        }
        for (MappedContext mappedContext : mappedHost.mapperContexts) {
            String prefix = "/" + mappedContext.name;
            if (url.startsWith(prefix)) {
                mappingData.setMappedContext(mappedContext);
                url = url.substring(prefix.length());
                break;
            }
        }
        MappedContext mappedContext = mappingData.getMappedContext();
        if (mappedContext == null) {
            return;
        }

        Map<String, HttpServlet> servletMap = mappedContext.object.getServletMap();
        HttpServlet httpServlet = servletMap.get(url);
        if (httpServlet == null) {
            url = url.replaceAll("/", File.separator);
            String filepath = mappedHost.object.getAppBaseFile().getAbsolutePath()
                    + File.separator + mappedContext.name
                    + File.separator + url;
            httpServlet = new StaticHttpServlet(new File(filepath));
        }

        mappingData.setHttpServlet(httpServlet);
    }

    public List<MappedHost> getMappedHosts() {
        return mappedHosts;
    }

    public void setMappedHosts(List<MappedHost> mappedHosts) {
        this.mappedHosts = mappedHosts;
    }

    public abstract static class MapElement<T> {

        public final String name;
        public final T object;

        public MapElement(String name, T object) {
            this.name = name;
            this.object = object;
        }
    }

    public static final class MappedHost extends MapElement<Host> {
        public List<MappedContext> mapperContexts = new ArrayList<>();

        public MappedHost(String name, Host host) {
            super(name, host);
        }
    }

    public static final class MappedContext extends MapElement<Context> {

        public MappedContext(String name, Context context) {
            super(name, context);
        }
    }
}
