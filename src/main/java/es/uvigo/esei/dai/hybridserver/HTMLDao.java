package es.uvigo.esei.dai.hybridserver;

import java.util.List;

public interface HTMLDao {
	String get(String uuid);
	List<String> list();
	void delete(String str);
	String create(String str);
}
