package com.github.spclock;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Crawler extends Thread{
    private CrawlerDao dao;

    public Crawler(CrawlerDao dao) {
        this.dao=dao;
    }
//private static CrawlerDao dao = new JdbcDao();

    @Override
    public void run() {
        try {
            String link;
            while ((link = dao.getNotProcessedLinkThenDelete()) != null) {
                if (dao.linkIsProcessed(link)) {
                    continue;
                }
                if (isInteresingLink(link)) {
                    Document doc = getHttpResponseEntity(link);
                    parseUrlsFromPageAndStoreIntoDB(doc);
                    AddIsNewPageToDB(doc, link);
                    dao.insertProcessedLink(link);
                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private void parseUrlsFromPageAndStoreIntoDB(Document doc) throws SQLException {
        for (Element aTag : doc.select("a")) {
            String href = aTag.attr("href");
            dao.insertNotProcessLink(href);
        }
    }


    private void AddIsNewPageToDB(Document doc, String link) throws SQLException {
        ArrayList<Element> articleTags = doc.select("article");
        if (!articleTags.isEmpty()) {
            for (Element articleTag : articleTags) {
                String title = articleTag.child(0).text();
                String content = articleTag.select("p").stream().map(Element::text).collect(Collectors.joining("\n"));
                dao.insertNewPage(link, title, content);
                System.out.println("title" + articleTag.child(0).text());
            }
        }
    }


    private Document getHttpResponseEntity(String link) throws IOException {

        link = processUrlNotHttps(link);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(link);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");

        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {

            System.out.println(link);
            HttpEntity entity1 = response1.getEntity();
            return Jsoup.parse(EntityUtils.toString(entity1));
        }
    }

    private String processUrlNotHttps(String link) {
        if (link.startsWith("//")) {
            link = "https:" + link;
        }
        return link;
    }

    private boolean isInteresingLink(String link) {
        return isNotLoginPage(link) && notSpecialPage(link)
                && (isNewPage(link) || isIndexPage(link));
    }

    private boolean notSpecialPage(String link) {
        //例子：  https:\/\/news.sina.cn\/news_zt\/keyword.d.html?k=王毅
        return !link.contains("javascript");
    }

    private boolean isNewPage(String link) {
        return link.contains("news.sina.cn");
    }

    private boolean isNotLoginPage(String link) {
        return !link.contains("passport.sina.cn");
    }

    private boolean isIndexPage(String link) {
        return "https://sina.cn".equals(link);
    }
}
