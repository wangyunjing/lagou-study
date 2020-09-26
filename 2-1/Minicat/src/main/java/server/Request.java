package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * 把请求信息封装为Request对象（根据InputSteam输入流封装）
 */
public class Request {

    private String method;
    private String url;
    private String host;

    private InputStream inputStream;

    public Request() {
    }

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;

        // 从输入流中获取请求信息
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }

        byte[] bytes = new byte[count];
        inputStream.read(bytes);

        String inputStr = new String(bytes);

        String[] split = inputStr.split("\\n");

        // 获取第一行请求头信息 GET / HTTP/1.1
        String firstLineStr = split[0];
        String[] strings = firstLineStr.split(" ");
        this.method = strings[0];
        this.url = strings[1];

        for (String str : split) {
            if (str.startsWith("Host: ")) {
                this.host = str.substring("Host: ".length());
                break;
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

}
