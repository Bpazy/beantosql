/**
 * Created by Ziyuan
 * on 2017/1/23
 */
public class MyField {
    private String name;
    private Class<?> type;

    public MyField(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public MyField setName(String name) {
        this.name = name;
        return this;
    }

    public Class<?> getType() {
        return type;
    }

    public MyField setType(Class<?> type) {
        this.type = type;
        return this;
    }
}
