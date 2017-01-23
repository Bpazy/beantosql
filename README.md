beantosql
=========
This is a util to simplify the process between SQL table and Java Bean.

Within the bean named `TestBean`
```
public class TestBean {
    private int id;
    private String name;
    private String content;
    private String longContent;
    private Date date;
}
```

You can run `new BeanToSQL().prettyPrinting().go(TestBean.class);`
So you may get a sql statement:
```
CREATE TABLE `TestBean` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `content` varchar(255) NOT NULL,
  `long_content` varchar(255) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`)
);
```

And then paste it into your DMS to create your table.



beantosql
=========
这是一款简化你在数据库的表和Java Bean之间同步修改的工具。

假设你有一个名字为`TestBean`的Java Bean
```
public class TestBean {
    private int id;
    private String name;
    private String content;
    private String longContent;
    private Date date;
}
```

你可以通过 `new BeanToSQL().prettyPrinting().go(TestBean.class);`
来获取创建表的SQL语句:
```
CREATE TABLE `TestBean` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `content` varchar(255) NOT NULL,
  `long_content` varchar(255) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`)
);
```