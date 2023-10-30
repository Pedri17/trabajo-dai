package es.uvigo.esei.dai.hybridserver;

public class HTMLController {
	private HTMLDao content;
	
	public HTMLController(HTMLDao contentStructure) {
		this.content = contentStructure;
	}
	
	public HTMLDao getContent() {
		return content;
	}
}
