package com.github.spclock;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.*;

public class JdbcDao implements CrawlerDao {
    private Connection connection;

    @SuppressFBWarnings("DMI_CONSTANT_DB_PASSWORD")
    public JdbcDao(String jdbcUrl) {
        try {
            connection = DriverManager.getConnection(jdbcUrl, "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNotProcessedLink(String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        return null;
    }


    @Override
    public String getNotProcessedLinkThenDelete() throws SQLException {
        String link = getNotProcessedLink("select * from LINKS_TO_BE_PROCESSED");
        try (PreparedStatement statement = connection.prepareStatement("delete from LINKS_TO_BE_PROCESSED where LINK = ?")) {
            statement.setString(1, link);
            statement.executeUpdate();
            return link;
        }
    }

    @Override
    public void insertNotProcessLink(String link) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into LINKS_TO_BE_PROCESSED (link) values (?)")) {
            statement.setString(1, link);
            statement.executeUpdate();
        }
    }

    @Override
    public void insertProcessedLink(String link) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into LINKS_ALREADY_PROCESSED (link )values (?)")) {
            statement.setString(1, link);
            statement.executeUpdate();
        }
    }

    @Override
    public boolean linkIsProcessed(String link) throws SQLException {
        boolean flag = false;
        ResultSet resultSet = null;
        try (PreparedStatement statement = connection.prepareStatement("select link from LINKS_ALREADY_PROCESSED where link =?")) {
            statement.setString(1, link);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                flag = true;
            }
            return flag;
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    @Override
    public void insertNewPage(String link, String title, String content) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("insert into NEWS (url,title,content )values (?,?,?)")) {
            statement.setString(1, link);
            statement.setString(2, title);
            statement.setString(3, content);
            statement.executeUpdate();
        }
    }


}
