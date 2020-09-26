package server;

import java.io.IOException;

public class LagouServlet1 extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        String content = "<h1>LagouServlet1 get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>LagouServlet1 post</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }
}
