package es.uvigo.esei.dai.hybridserver.xml;

import java.util.List;

public interface XMLDao {
	public String get(String uuid) throws Exception;
	public List<String> list() throws Exception;
	public void delete(String str) throws Exception;
	public String create(String xmlContent) throws Exception;
}
