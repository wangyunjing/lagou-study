package server;

import java.io.File;
import java.io.IOException;

/**
 * @author yunjing.wang
 * @date 2020/9/27
 */
public class StaticHttpServlet extends HttpServlet {
    private File file;

    public StaticHttpServlet(File file) {
        this.file = file;
    }

    @Override
    public void doGet(Request request, Response response) {
        try {
            response.outputHtml(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        try {
            response.outputHtml(file);
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
