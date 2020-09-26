package mapper;

import server.HttpServlet;

/**
 * @author yunjing.wang
 * @date 2020/9/27
 */
public class MappingData {

    private Mapper.MappedHost mappedHost;
    private Mapper.MappedContext mappedContext;
    private HttpServlet httpServlet;

    public Mapper.MappedHost getMappedHost() {
        return mappedHost;
    }

    public void setMappedHost(Mapper.MappedHost mappedHost) {
        this.mappedHost = mappedHost;
    }

    public Mapper.MappedContext getMappedContext() {
        return mappedContext;
    }

    public void setMappedContext(Mapper.MappedContext mappedContext) {
        this.mappedContext = mappedContext;
    }

    public HttpServlet getHttpServlet() {
        return httpServlet;
    }

    public void setHttpServlet(HttpServlet httpServlet) {
        this.httpServlet = httpServlet;
    }
}
