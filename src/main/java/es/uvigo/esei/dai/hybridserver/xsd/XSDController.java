package es.uvigo.esei.dai.hybridserver.xsd;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.ControllerInterface;

public class XSDController implements ControllerInterface{
	private XSDDao content;
	
	public XSDController(XSDDao contentStructure) {
		this.content = contentStructure;
	}
	
	public XSDDao getData() {
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
