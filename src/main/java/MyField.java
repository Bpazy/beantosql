/**
 * Created by Ziyuan
 * on 2017/1/23
 */
public class MyField {
    private String name;
    private boolean named;
    private Class<?> type;
    private boolean primary;
    public MyField(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }
    public MyField() {
    }

    public boolean isNamed() {
        return named;
    }

    public MyField setNamed(boolean named) {
        this.named = named;
        return this;
    }

    public boolean isPrimary() {
        return primary;
    }

    public MyField setPrimary(boolean primary) {
        this.primary = primary;
        return this;
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
