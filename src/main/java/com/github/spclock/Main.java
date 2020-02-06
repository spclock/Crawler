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
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> linkPool = new ArrayList<>();
        Set<String> handledPool = new HashSet<>();

        linkPool.add("https://sina.cn");
        while (!linkPool.isEmpty()) {
            String link = linkPool.remove(linkPool.size() - 1);
            if (handledPool.contains(link)) {
                continue;
            }
            if (isInteresingLink(link)) {
                //是新闻我们处理

                Document doc = getHttpResponseEntity(link);

                doc.select("a").stream().map(aTag -> aTag.attr("href")).forEach(linkPool::add);

                AddIsNewPageToDB(doc);

                handledPool.add(link);

            } else {
                //不是新闻
            }
        }
    }

    private static void AddIsNewPageToDB(Document doc) {
        ArrayList<Element> articleTags = doc.select("article");
        if (!articleTags.isEmpty()) {
            for (Element articleTag : articleTags) {
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
            System.out.println(response1.getStatusLine());
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
        return isNotLoginPage(link) && isNotSpecialPage(link)
                && (isNewPage(link) || isIndexPage(link));
    }

    private static boolean isNotSpecialPage(String link) {
        //例子：  https:\/\/news.sina.cn\/news_zt\/keyword.d.html?k=王毅
        return !link.contains("keyword.d.html");
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
