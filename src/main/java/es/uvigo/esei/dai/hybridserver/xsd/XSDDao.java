package es.uvigo.esei.dai.hybridserver.xsd;

import java.util.List;

public interface XSDDao {
	public String get(String uuid) throws Exception;
	public List<String> list() throws Exception;
	public void delete(String str) throws Exception;
	public String create(String xsdContent) throws Exception;
}
