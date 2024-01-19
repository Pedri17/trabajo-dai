package es.uvigo.esei.dai.hybridserver.html;

import java.util.List;

public interface HTMLDao {
	public String get(String uuid) throws Exception;
	public List<String> list() throws Exception;
	public void delete(String str) throws Exception;
	public String create(String str) throws Exception;
}
