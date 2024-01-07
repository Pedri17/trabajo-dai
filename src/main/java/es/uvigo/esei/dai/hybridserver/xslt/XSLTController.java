package es.uvigo.esei.dai.hybridserver.xslt;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.ControllerInterface;

public class XSLTController implements ControllerInterface {
	private XSLTDao content;
	
	public XSLTController(XSLTDao contentStructure) {
		this.content = contentStructure;
	}
	
	public XSLTDao getData() {
		return content;
	}

	public String getContent(String uuid){
		return content.getContent(uuid);
	}

	public List<String> list(){
		return content.list();
	}

	public void delete(String uuid){
		content.delete(uuid);
	}

	public String getXsd(String uuid){
		return content.getXsd(uuid);
	}

	public boolean contains(String uuid){
		return content.getContent(uuid) != null;
	}
}
