package models.dao;


import common.exceptions.MyException;
import models.connectors.ConnectionDB;
import models.pojo.Article;
import models.pojo.Topic;
import models.pojo.User;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Павел on 25.02.2017.
 */
public class ArticleDao {
    private static Logger logger = Logger.getLogger(ArticleDao.class);
    public static boolean addArticle(Article article) throws MyException {
        try{
            Connection connection = ConnectionDB.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO articles " +
                "(title, text, publicationdate, userId, topicId) " +
                "VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, article.getTitle());
            preparedStatement.setString(2, article.getTextArticle());
            preparedStatement.setString(3, article.getDatePublication());
            preparedStatement.setInt(4, article.getUser().getId());
            preparedStatement.setInt(5, article.getTopic().getId());
            if(preparedStatement.execute()){
                return true;
            }
            } catch (SQLException e) {
                logger.error(e);
            throw new MyException("Sorry, we have some problem with our system!");
            }
        return false;
    }

    public static boolean deleteArticle(Article article) throws MyException {
        try {
            Connection connection = ConnectionDB.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM articles WHERE id = ?");
            preparedStatement.setInt(1, article.getId());
            if(preparedStatement.execute()){
                return true;
            }
        }catch (SQLException e){
            logger.error(e);
            throw new MyException("Sorry, we have some problem with our system!");
        }
        return false;
    }

    public static Article getArticleById(int id) throws MyException {
        Article article = new Article();
        User user = new User();
        UserDao userDB = new UserDao();
        Topic topic = new Topic();
        TopicDao topicDB =new TopicDao();
        try {
            Connection connection = ConnectionDB.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM articles where id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                article.setId(resultSet.getInt(1));
                article.setTitle(resultSet.getString(2));
                article.setTextArticle(resultSet.getString(3));
                article.setDatePublication(resultSet.getString(4));
                user = userDB.getUserById(resultSet.getInt(5));
                if (user != null) {
                    article.setUser(user);
                }
                topic = topicDB.getTopicById(resultSet.getInt(6));
                if (topic != null) {
                    article.setTopic(topic);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            throw new MyException("Sorry, we have some problem with our system!");
        }
        return article;
    }

    public static ArrayList<Article> getArticleByUserId(int userId) throws MyException {
        try {
            Connection connection = ConnectionDB.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM articles WHERE userId = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getArticles(resultSet);
        }catch (SQLException e){
            logger.error(e);
            throw new MyException("Sorry, we have some problem with our system!");
        }
    }

    public static ArrayList<Article> getArticleByTopicId(int topicId) throws MyException {
        try {
            Connection connection = ConnectionDB.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM articles WHERE topicId = ?");
            preparedStatement.setInt(1, topicId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getArticles(resultSet);
        }catch (SQLException e){
            logger.error(e);
            throw new MyException("Sorry, we have some problem with our system!");
        }
    }


    public static ArrayList<Article> getAllArticle() throws MyException {
        try {
            Connection connection = ConnectionDB.getConnectionDB();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM articles");
            ResultSet resultSet = preparedStatement.executeQuery();
            return getArticles(resultSet);
        }catch (SQLException e){
            logger.error(e);
            throw new MyException("Sorry, we have some problem with our system!");
        }
    }

    private static ArrayList<Article> getArticles(ResultSet resultSet) throws MyException {
        User user;
        Topic topic;
        ArrayList<Article> articles = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Article article = new Article();
                article.setId(resultSet.getInt(1));
                article.setTitle(resultSet.getString(2));
                article.setTextArticle(resultSet.getString(3));
                article.setDatePublication(resultSet.getString(4));
                user = UserDao.getUserById(resultSet.getInt(5));
                if (user != null) {
                    article.setUser(user);
                }
                topic = TopicDao.getTopicById(resultSet.getInt(6));
                if (topic != null) {
                    article.setTopic(topic);
                }
                articles.add(article);
            }
        }catch (SQLException e){
            logger.error(e);
            throw new MyException("Sorry, we have some problem with our system!");
        }
        return articles;
    }
}
