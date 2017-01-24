import annotation.Name;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Created by Ziyuan
 * on 2017/1/23
 */
public class TestConvert {

    @Test
    public void convert() {
        String go = new BeanToSQL().prettyPrinting().go(TestBean.class);
        System.out.println(go);
    }

    @Test
    public void test() {
        Class<TestBean> clazz = TestBean.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            Name name = f.getDeclaredAnnotation(Name.class);
            if (name != null) {
                System.out.println(f.getName() + name.value());
            }
        }
    }
}
