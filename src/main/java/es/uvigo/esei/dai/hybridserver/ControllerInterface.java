package es.uvigo.esei.dai.hybridserver;

import java.util.List;

public interface ControllerInterface {
    public String getContent(String uuid);
    public List<String> list();
	public boolean contains(String uuid);
    public void delete(String str);
}
