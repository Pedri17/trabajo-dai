package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HTMLDaoDB implements HTMLDao {
    private String USER;
    private String PASSWORD;
    private String URL;
    
    public HTMLDaoDB (String user, String password, String url) {
        USER = user;
        PASSWORD = password;
        URL = url;
    }

    
    @Override
    public String get(String uuid) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT content FROM HTML WHERE uuid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("content");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }

        return null;
    }

    @Override
    public List<String> list() {
        List<String> htmlList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT uuid FROM HTML";
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        htmlList.add(resultSet.getString("uuid"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return htmlList;
    }

    @Override
    public void delete(String uuid) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "DELETE FROM HTML WHERE uuid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
    }

    @Override
    public String create(String htmlContent) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String uuid = generateUUID();

            String sql = "INSERT INTO HTML (uuid, content) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                preparedStatement.setString(2, htmlContent);
                preparedStatement.executeUpdate();
            }
            return uuid;
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return null;
    }

    private String generateUUID() {
        String uuid = null;
        do {
            uuid = UUID.randomUUID().toString();
        } while (uuidExistsInDatabase(uuid));
        return uuid;
    }

    private boolean uuidExistsInDatabase(String uuid) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT COUNT(*) FROM HTML WHERE uuid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
          
        }
        return false;
    }
}