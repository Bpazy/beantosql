import annotation.Name;
import annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //TODO 解析类字符串
    public String go(String clazz) {
        try {
            String className = getClassName(clazz);
            List<MyField> fields = getFields(clazz);
            System.out.println(className);
            fields.forEach(field -> System.out.println(field.getType() + " " + field.getName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getClassName(String clazz) {
        Pattern pattern = Pattern.compile("(?:class)(.+)(?:\\{)");
        Matcher matcher = pattern.matcher(clazz);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private List<MyField> getFields(String clazz) throws ClassNotFoundException {
        List<MyField> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?:(?:pubic)|(?:private)) (.+?) (.+?);");
        Matcher matcher = pattern.matcher(clazz);
        while (matcher.find()) {
            MyField field = new MyField();
            String classWithPackage = getPackageClass(matcher);
            field.setName(matcher.group(2))
                    .setType(Class.forName(classWithPackage));
            list.add(field);
        }
        return list;
    }

    private String getPackageClass(Matcher matcher) throws ClassNotFoundException {
        switch (matcher.group(1)) {
            case "String":
                return "java.lang.String";
            case "int":
                return "java.lang.Integer";
            case "Date":
                return "java.util.Date";
            default:
                throw new ClassNotFoundException();
        }
    }

    private List<MyField> getFields(Class c) {
        Field[] declaredFields = c.getDeclaredFields();
        List<MyField> fields = new ArrayList<>();
        for (Field f : declaredFields) {
            Name name = f.getDeclaredAnnotation(Name.class);
            PrimaryKey primaryKey = f.getDeclaredAnnotation(PrimaryKey.class);
            boolean isPrimary = primaryKey != null;

            MyField field = new MyField();
            field.setPrimary(isPrimary);
            if (name != null) {
                field.setName(name.value())
                        .setNamed(true)
                        .setType(f.getType());
            } else {
                field.setName(f.getName())
                        .setType(f.getType());
            }
            fields.add(field);
        }
        return fields;
    }

    private String createSQL(Class c, List<MyField> fields) {
        String tableName = c.getName();
        String tableNameSql = String.format("CREATE TABLE `%s`", tableName);

        StringBuilder fieldSQLBuilder = new StringBuilder();
        List<String> primaries = new ArrayList<>();
        fields.forEach(field -> {
            String fieldName = fieldConvert(field);
            String fieldTypeName = typeConvert(field.getType().getName());
            if (field.isPrimary()) { // if the key is primary key, it will be added to list for the next handle.
                primaries.add(fieldName);
            }
            fieldSQLBuilder.append(
                    String.format("`%s` %s NOT NULL,",
                            fieldName,
                            fieldTypeName));
        });
        handlePrimaries(fieldSQLBuilder, primaries);

        return String.format("%s (%s);", tableNameSql, fieldSQLBuilder.toString());
    }

    private void handlePrimaries(StringBuilder fieldSQLBuilder, List<String> primaries) {
        if (primaries.size() == 0) {
            fieldSQLBuilder.append("PRIMARY KEY (`id`)");
        }
        primaries.forEach(primaryName ->
                fieldSQLBuilder.append(
                        String.format("PRIMARY KEY (`%s`)", primaryName)));
    }

    private String fieldConvert(MyField field) {
        if (field.isNamed()) {
            return field.getName();
        }
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
