package server;

import mapper.Mapper;
import mapper.MappingData;

import java.io.InputStream;
import java.net.Socket;

public class RequestProcessor implements Runnable {

    private Socket socket;
    private Mapper mapper;

    public RequestProcessor(Socket socket, Mapper mapper) {
        this.socket = socket;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            MappingData mappingData = new MappingData();
            mapper.map(request.getHost(), request.getUrl(), mappingData);

            HttpServlet httpServlet = mappingData.getHttpServlet();
            if (httpServlet == null) {
                // 输出404
                response.output(HttpProtocolUtil.getHttpHeader404());
            } else {
                httpServlet.service(request, response);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
