package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/9/26
 */
public class Host {
    private Service service;
    private String name;
    private String appBase;

    private List<Context> contexts = new ArrayList<>();

    public void start() {
        File[] files = getAppBaseFile().listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                Context context = new Context(this, file.getName());
                contexts.add(context);
            }
        }

        for (Context context : contexts) {
            context.start();
        }
    }

    public File getAppBaseFile() {
        String filePath = this.getClass().getClassLoader().getResource("").getFile() + appBase;
        return new File(filePath);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppBase() {
        return appBase;
    }

    public void setAppBase(String appBase) {
        this.appBase = appBase;
    }

    public List<Context> getContexts() {
        return contexts;
    }

    public void setContexts(List<Context> contexts) {
        this.contexts = contexts;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
