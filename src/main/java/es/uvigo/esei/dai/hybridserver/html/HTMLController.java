package es.uvigo.esei.dai.hybridserver.html;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.ControllerInterface;

public class HTMLController implements ControllerInterface {
	private HTMLDao content;
	
	public HTMLController(HTMLDao contentStructure) {
		this.content = contentStructure;
	}
	
	public HTMLDao getData() {
		return content;
	}

	public List<String> list(){
		return content.list();
	}

	public void delete(String uuid){
		content.delete(uuid);
	}

	public String getContent(String uuid){
		return content.get(uuid);
	}

	public boolean contains(String uuid){
		return content.get(uuid) != null;
	}
}
