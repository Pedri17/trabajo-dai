package es.uvigo.esei.dai.hybridserver.xsd;

public class XSDController {
	private XSDDao content;
	
	public XSDController(XSDDao contentStructure) {
		this.content = contentStructure;
	}
	
	public XSDDao getData() {
		return content;
	}

	public String getContent(String uuid){
		return content.get(uuid);
	}

	public boolean contains(String uuid){
		return content.get(uuid) != null;
	}
}
