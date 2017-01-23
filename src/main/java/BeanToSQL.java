import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Ziyuan
 * on 2017/1/23
 */
public class BeanToSQL {
    private boolean prettyPrinting = false;

    public BeanToSQL prettyPrinting() {
        prettyPrinting = true;
        return this;
    }

    private String prettyOutput(String sql) {
        Stack<Character> stack = new Stack<>();
        StringBuilder builder = new StringBuilder();
        char[] array = sql.toCharArray();
        for (char c : array) {
            syntactic(stack, builder, c);
        }
        if (!stack.empty()) {
            throw new SyntacticException();
        }
        return builder.toString();
    }

    private void syntactic(Stack<Character> stack, StringBuilder builder, char c) {
        switch (c) {
            case '(':
                if (stack.empty()) {
                    builder.append(c);
                    builder.append('\n');
                    stack.push(c);
                    appendBlank(stack, builder);
                } else {
                    builder.append(c);
                    stack.push(c);
                }
                break;
            case ')':
                if (stack.size() == 1) {
                    builder.append('\n');
                    builder.append(c);
                } else {
                    builder.append(c);
                }
                stack.pop();
                break;
            case ',':
                builder.append(c);
                builder.append('\n');
                appendBlank(stack, builder);
                break;
            default:
                builder.append(c);
        }
    }

    private void appendBlank(Stack<Character> stack, StringBuilder builder) {
        for (int i = 0; i < stack.size(); i++) {
            builder.append("  ");
        }
    }

    public String go(Class c) {
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
            String fieldName = fieldConvert(field);
            String fieldTypeName = typeConvert(field.getType().getName());
            fieldSQLBuilder.append(
                    String.format("`%s` %s NOT NULL,",
                            fieldName,
                            fieldTypeName));
        });
        fieldSQLBuilder.append("PRIMARY KEY (`id`)");

        return String.format("%s (%s);", tableNameSql, fieldSQLBuilder.toString());
    }

    private String fieldConvert(MyField field) {
        String name = field.getName();
        StringBuilder builder = new StringBuilder(name);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (c > 64 && c < 91) {
                char lowerC = (char) (c + 32);
                builder.replace(i, i + 1, "_" + lowerC);
            }
        }
        return builder.toString();
    }

    private String typeConvert(String origin) {
        switch (origin) {
            case "int":
                return "int";
            case "java.lang.String":
                return "varchar(255)";
            case "java.util.Date":
                return "datetime";
            default:
                return "";
        }
    }
}
