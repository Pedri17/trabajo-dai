package es.uvigo.esei.dai.hybridserver;

import java.util.List;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public List<String> list() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }

    @Override
    public void delete(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public String create(String str) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }
}