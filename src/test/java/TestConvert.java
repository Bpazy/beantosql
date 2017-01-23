import org.junit.Test;

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
}
