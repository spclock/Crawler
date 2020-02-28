# 多线程爬虫和elasticsearch数据分析项目

爬虫爬的是sina.cn网页的新闻信息，如何爬的结构是写死的  

爬到的数据会存储到数据库中，你可以用你想用的数据库来存储爬到新闻信息

你需要数据库迁移，在你的idea上git bash终端输入
```bash
mvn flyway:migrate
```
```xml
<!--在pom.xml-->
<!-- 注意：文件的通配符问题，我这个是写死了-->
<url>jdbc:h2:file:e:/crawler/news</url>
```

使用docker 
```
命令参数根据自己情况来配置
$ docker run --name some-mysql -v /my/own/datadir:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql:tag
我自己配置例子
$ docker run --name mysql -v mysql_data:/etc/mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:5.7.27
-v
具体在哪儿不用管，反正它就是被保存起来了
```
```xml
<!--在pom.xml-->
<!-- 注意：文件的通配符问题，我这个是写死了-->
<url>jdbc:mysql://192.168.99.100:3306/news?useUnicode=true&amp;characterEncoding=UTF-8</url>
<!--192.168.99.100是我docker ip，换成你自己-->
```


如果你想修改代码，提交建议请通过我们的检查测试
1. checkstyle
2. spotbugs

请输入`mvn verify`  来检查你的代码
