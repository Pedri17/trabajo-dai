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
    public String get(String uuid) throws SQLException {
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
            throw e;
        }

        return null;
    }

    @Override
    public List<String> list() throws SQLException {
        List<String> xmlList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT uuid FROM XML";
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        xmlList.add(resultSet.getString("uuid"));
                    }
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return xmlList;
    }

    @Override
    public void delete(String uuid) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "DELETE FROM XML WHERE uuid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public String create(String xmlContent) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String uuid = generateUUID();

            String sql = "INSERT INTO XML (uuid, content) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid);
                preparedStatement.setString(2, xmlContent);
                preparedStatement.executeUpdate();
            }
            return uuid;
        } catch (SQLException e) {
            throw e;
        }
    }

    private String generateUUID() throws SQLException {
        String uuid = null;
        try{
            do {
                uuid = UUID.randomUUID().toString();
            } while (uuidExistsInDatabase(uuid));
        }catch(SQLException e){
            throw e;
        }
        return uuid;
    }

    private boolean uuidExistsInDatabase(String uuid) throws SQLException {
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
            throw e;
        }
        return false;
    }
}