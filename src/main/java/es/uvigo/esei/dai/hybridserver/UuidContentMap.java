package es.uvigo.esei.dai.hybridserver;

import java.util.Map;
import java.util.TreeMap;

public class UuidContentMap {
	Map<String, String> uuidContentMap;
	
	public UuidContentMap() {
		uuidContentMap = new TreeMap<String, String>();
	}
	
	public UuidContentMap(Map<String, String> map) {
		uuidContentMap = map;
	}
	
	public String getContent(String uuid) {
		return uuidContentMap.get(uuid);
	}
	
	public boolean insertUUID(String uuid, String content) {
		if(!uuidContentMap.containsKey(uuid)) {
			uuidContentMap.put(uuid, content);
			return true;
		}else {
			return false;
		}
		
	}
}
