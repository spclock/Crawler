package com.github.spclock;

import java.sql.SQLException;

public interface DataAccessObject {

    String getNotProcessedLink(String sql) throws SQLException;

    boolean judgeLinkIsNull() throws SQLException;

    String getNotProcessedLinkThenDelete() throws SQLException;

    void addNotProcessLink(String link) throws SQLException;

    void addProcessedLink(String link) throws SQLException;

    boolean linkIsProcessed(String link) throws SQLException;

    void addNewPage(String link, String title, String content) throws SQLException;


}
