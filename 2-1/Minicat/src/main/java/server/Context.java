package server;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yunjing.wang
 * @date 2020/9/26
 */
public class Context {

    private Host host;
    private String prefix;

    private Map<String, HttpServlet> servletMap = new HashMap<String, HttpServlet>();

    public Context(Host host, String prefix) {
        this.host = host;
        this.prefix = prefix;
    }

    public void start() {
        // 加载解析相关的配置，web.xml
        try {
            loadServlet();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private void loadServlet() throws FileNotFoundException {
        InputStream resourceAsStream = new FileInputStream(
                new File(host.getAppBaseFile().getAbsolutePath() + File.separator + prefix + File.separator + "web.xml"));
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                // <servlet-name>lagou</servlet-name>
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                // <servlet-class>server.LagouServlet</servlet-class>
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();
                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /lagou
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, HttpServlet> getServletMap() {
        return servletMap;
    }

    public void setServletMap(Map<String, HttpServlet> servletMap) {
        this.servletMap = servletMap;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
