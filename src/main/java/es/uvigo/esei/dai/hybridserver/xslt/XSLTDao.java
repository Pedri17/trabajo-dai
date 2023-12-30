package es.uvigo.esei.dai.hybridserver.xslt;

import java.util.List;

public interface XSLTDao {
	String getContent(String uuid);
	String getXsd(String uuid);
	List<String> list();
	void delete(String str);
	String create(String xsltContent, String xsdUuid);
}
