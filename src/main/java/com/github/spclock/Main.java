package com.github.spclock;

public class Main {

    public static void main(String[] args) {
        CrawlerDao dao = new MybatisDao();
        for (int i = 0; i < 10; i++) {
            new Crawler(dao).start();
        }
    }
}
