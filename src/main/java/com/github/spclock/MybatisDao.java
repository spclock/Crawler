package com.github.spclock;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
public class MybatisDao implements CrawlerDao {

    private SqlSessionFactory sqlSessionFactory;

    public MybatisDao() {
        try {
            String resource = "db/mybatis/config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized String getNotProcessedLinkThenDelete() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            String url = session.selectOne("com.github.spclock.MyMapper.selectNextAvailableLink");
            if (url != null) {
                session.delete("com.github.spclock.MyMapper.deleteLink", url);
            }
            return url;
        }
    }

    @Override
    public void insertNotProcessLink(String link) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "LINKS_TO_BE_PROCESSED");
        param.put("link", link);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.spclock.MyMapper.insertLink", param);
        }
    }

    @Override
    public void insertProcessedLink(String link) {
        Map<String, Object> param = new HashMap<>();
        param.put("tableName", "LINKS_ALREADY_PROCESSED");
        param.put("link", link);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.spclock.MyMapper.insertLink", param);
        }
    }

    @Override
    public boolean linkIsProcessed(String link) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            int count = session.selectOne("com.github.spclock.MyMapper.countLink", link);
            return count != 0;
        }

    }

    @Override
    public void insertNewPage(String url, String title, String content) {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            session.insert("com.github.spclock.MyMapper.insertNews", new News(url, title, content));
        }
    }
}
