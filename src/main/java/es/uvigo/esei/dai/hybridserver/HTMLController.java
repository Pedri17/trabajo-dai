package es.uvigo.esei.dai.hybridserver;

public class HTMLController {
	private HTMLDao content;
	
	public HTMLController(HTMLDao contentStructure) {
		this.content = contentStructure;
	}
	
	public HTMLDao getData() {
		return content;
	}

	public String getContent(String uuid){
		return content.get(uuid);
	}

	public boolean contains(String uuid){
		return content.get(uuid) != null;
	}
}
