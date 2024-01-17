package es.uvigo.esei.dai.hybridserver;

import java.util.List;

public interface ControllerInterface {
    public String getContent(String uuid) throws Exception;
    public List<String> list() throws Exception;
	public boolean contains(String uuid) throws Exception;
    public void delete(String str) throws Exception;
}
