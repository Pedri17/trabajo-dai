package es.uvigo.esei.dai.hybridserver.xml;

public class XMLController {
	private XMLDao content;
	
	public XMLController(XMLDao contentStructure) {
		this.content = contentStructure;
	}
	
	public XMLDao getData() {
		return content;
	}

	public String getContent(String uuid){
		return content.get(uuid);
	}

	public boolean contains(String uuid){
		return content.get(uuid) != null;
	}
}
