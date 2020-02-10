package com.github.spclock;

import java.sql.SQLException;

public interface CrawlerDao {


    String getNotProcessedLinkThenDelete() throws SQLException;

    void insertNotProcessLink(String link) throws SQLException;

    void insertProcessedLink(String link) throws SQLException;

    boolean linkIsProcessed(String link) throws SQLException;

    void insertNewPage(String link, String title, String content) throws SQLException;


}
