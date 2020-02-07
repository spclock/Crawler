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

public class Main {
    private static H2Dao dao = new H2Dao("jdbc:h2:E:/test/Crawler/news");

    public static void main(String[] args) throws IOException, SQLException {
//        List<String> linkPool = new ArrayList<>();
//        Set<String> handledPool = new HashSet<>();
//
//        linkPool.add("https://sina.cn");


        while (!dao.judgeLinkIsNull()) {

//            String link = linkPool.remove(linkPool.size() - 1);
            String link = dao.getNotProcessedLinkThenDelete();


//            if (handledPool.contains(link)) {
            if (dao.linkIsProcessed(link)) {
                continue;
            }
            if (isInteresingLink(link)) {
                //是新闻我们处理

                Document doc = getHttpResponseEntity(link);

                for (Element aTag : doc.select("a")) {
                    String href = aTag.attr("href");
//                    linkPool.add(href);
                    dao.addNotProcessLink(href);
                }

                AddIsNewPageToDB(doc, link);

                dao.addProcessedLink(link);
//                handledPool.add(link);

            } else {
                //不是新闻
            }
        }
    }

    private static void AddIsNewPageToDB(Document doc, String link) throws SQLException {
        ArrayList<Element> articleTags = doc.select("article");
        if (!articleTags.isEmpty()) {
            for (Element articleTag : articleTags) {
                String title = articleTag.child(0).text();
                String content = articleTag.select("p").stream().map(Element::text).collect(Collectors.joining("\n"));
                dao.addNewPage(link, title, content);
                System.out.println("title" + articleTag.child(0).text());
            }
        }
    }


    private static Document getHttpResponseEntity(String link) throws IOException {

        link = processUrlNotHttps(link);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(link);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");

        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
//            System.out.println(response1.getStatusLine());
            System.out.println(link);
            HttpEntity entity1 = response1.getEntity();
            return Jsoup.parse(EntityUtils.toString(entity1));
        }
    }

    private static String processUrlNotHttps(String link) {
        if (link.startsWith("//")) {
            link = "https:" + link;
        }
        return link;
    }

    private static boolean isInteresingLink(String link) {
        return isNotLoginPage(link) && notSpecialPage(link)
                && (isNewPage(link) || isIndexPage(link));
    }

    private static boolean notSpecialPage(String link) {
        //例子：  https:\/\/news.sina.cn\/news_zt\/keyword.d.html?k=王毅
        return !link.contains("javascript");
    }

    private static boolean isNewPage(String link) {
        return link.contains("news.sina.cn");
    }

    private static boolean isNotLoginPage(String link) {
        return !link.contains("passport.sina.cn");
    }

    private static boolean isIndexPage(String link) {
        return "https://sina.cn".equals(link);
    }
}
