package es.uvigo.esei.dai.hybridserver.xml;

import java.util.List;

public interface XMLDao {
	String get(String uuid);
	List<String> list();
	void delete(String str);
	String create(String uuid, String xsd);
}
