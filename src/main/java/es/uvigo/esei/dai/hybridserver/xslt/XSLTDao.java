package es.uvigo.esei.dai.hybridserver.xslt;

import java.util.List;

public interface XSLTDao {
	String get(String uuid);
	List<String> list();
	void delete(String str);
	String create(String uuid, String xslt, String xsd);
}
