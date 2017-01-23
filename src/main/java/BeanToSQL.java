import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ziyuan
 * on 2017/1/23
 */
public class BeanToSQL {
    private boolean prettyPrinting = false;

    private BeanToSQL prettyPrinting() {
        prettyPrinting = true;
        return this;
    }

    private String prettyOutput(String sql) {
        StringBuilder builder = new StringBuilder(sql);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if ((c == '(' && builder.charAt(i - 2) == '`') || c == ',') {
                builder.insert(i + 1, '\n');
                i++;
            }
        }
        return builder.toString();
    }

    private String go(Class c) {
        List<MyField> fieldNames = getFields(c);
        String sql = createSQL(c, fieldNames);
        return prettyPrinting ? prettyOutput(sql) : sql;
    }

    private List<MyField> getFields(Class c) {
        java.lang.reflect.Field[] declaredFields = c.getDeclaredFields();
        List<MyField> fields = new ArrayList<>();
        for (java.lang.reflect.Field f : declaredFields) {
            MyField field = new MyField(f.getName(), f.getType());
            fields.add(field);
        }
        return fields;
    }

    private String createSQL(Class c, List<MyField> fields) {
        String tableName = c.getName();
        String tableNameSql = String.format("CREATE TABLE `%s`", tableName);

        StringBuilder fieldSQLBuilder = new StringBuilder();
        fields.forEach(field -> {
            fieldSQLBuilder.append(
                    String.format("`%s` %s NOT NULL,",
                            field.getName(),
                            typeConvert(field.getType().getName())));
        });
        fieldSQLBuilder.append("PRIMARY KEY (`id`)");

        return String.format("%s (%s);", tableNameSql, fieldSQLBuilder.toString());
    }

    private String typeConvert(String origin) {
        switch (origin) {
            case "int":
                return "int";
            case "java.lang.String":
                return "varchar(255)";
            default:
                return "";
        }
    }
}
