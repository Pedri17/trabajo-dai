package es.uvigo.esei.dai.hybridserver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.transform.Source;

public class HTMLDaoMap implements HTMLDao {
	Map<String, String> uuidContentMap;
	
	public HTMLDaoMap() {
		uuidContentMap = new TreeMap<String, String>();
	}
	
	public HTMLDaoMap(Map<String, String> map) {
		uuidContentMap = map;
	}
	
	public String get(String uuid) {
		return uuidContentMap.get(uuid);
	}
	
	public List<String> list(){
		return new LinkedList<String>(uuidContentMap.keySet());
	}
	
	public String create(String content) {
		UUID uuid = UUID.randomUUID();
		uuidContentMap.put(uuid.toString(), content);
		return uuid.toString();
	}
	
	public void delete(String uuid) {
		uuidContentMap.remove(uuid);
	}
}
