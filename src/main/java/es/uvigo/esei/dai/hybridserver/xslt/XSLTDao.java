package es.uvigo.esei.dai.hybridserver.xslt;

import java.util.List;

public interface XSLTDao {
	public String getContent(String uuid) throws Exception;
	public String getXsd(String uuid) throws Exception;
	public List<String> list() throws Exception;
	public void delete(String str) throws Exception;
	public String create(String xsltContent, String xsdUuid) throws Exception;
}
