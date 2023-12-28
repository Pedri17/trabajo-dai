package es.uvigo.esei.dai.hybridserver.xml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XMLDaoDB implements XMLDao {
    private String user;
    private String password;
    private String url;
    
    public XMLDaoDB (String user, String password, String url) {
        this.user = user;
        this.password = password;
        this.url = url;
    }

    
    @Override
    public String get(String uuid) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT content FROM XML WHERE uuid = ?";
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
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT uuid FROM XML";
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
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "DELETE FROM XML WHERE uuid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String create(String content, String xml) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String uuid = generateUUID();

            String sql = "INSERT INTO XML (uuid, content) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                preparedStatement.setString(2, xml);
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
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT COUNT(*) FROM XML WHERE uuid = ?";
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