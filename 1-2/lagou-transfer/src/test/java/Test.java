import com.lagou.edu.factory.AnnotationApplicationContext;

/**
 * @author yunjing.wang
 * @date 2020/8/16
 */
public class Test {

    @org.junit.Test
    public void test() {
        AnnotationApplicationContext context = new AnnotationApplicationContext("com.lagou.edu");
        context.refresh();
        System.out.println(context);

    }

}
