package es.uvigo.esei.dai.hybridserver.xsd;

import java.util.List;

public interface XSDDao {
	String get(String uuid);
	List<String> list();
	void delete(String str);
	String create(String xsdContent);
}
