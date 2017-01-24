import annotation.Name;
import annotation.PrimaryKey;

import java.util.Date;

/**
 * Created by Ziyuan
 * on 2017/1/23
 */
public class TestBean {
    @Name("id123")
    @PrimaryKey
    private int id;
    private String name;
    private String content;
    @Name("LONG_CONTENT")
    private String longContent;
    private Date date;
}
