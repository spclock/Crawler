# 多线程爬虫和elasticsearch数据分析项目

爬虫爬的是sina.cn网页的新闻信息，如何爬的结构是写死的  

爬到的数据会存储到数据库中，你可以用你想用的数据库来存储爬到新闻信息

你需要数据库迁移，在你的idea上git bash终端输入
```bash
mvn flyway:migrate
```

如果你想修改代码，提交建议请通过我们的检查测试
1. checkstyle
2. spotbugs

请输入`mvn verify`  来检查你的代码