package es.uvigo.esei.dai.hybridserver.xslt;

public class XSLTController {
	private XSLTDao content;
	
	public XSLTController(XSLTDao contentStructure) {
		this.content = contentStructure;
	}
	
	public XSLTDao getData() {
		return content;
	}

	public String getContent(String uuid){
		return content.get(uuid);
	}

	public boolean contains(String uuid){
		return content.get(uuid) != null;
	}
}
