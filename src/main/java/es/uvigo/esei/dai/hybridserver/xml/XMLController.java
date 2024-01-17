package es.uvigo.esei.dai.hybridserver.xml;

import java.util.List;

import es.uvigo.esei.dai.hybridserver.ControllerInterface;

public class XMLController implements ControllerInterface {
	private XMLDao content;
	
	public XMLController(XMLDao contentStructure) {
		this.content = contentStructure;
	}
	
	public XMLDao getData() {
		return content;
	}

	public List<String> list() throws Exception{
		return content.list();
	}

	public void delete(String uuid) throws Exception{
		content.delete(uuid);
	}

	public String getContent(String uuid) throws Exception{
		return content.get(uuid);
	}

	public boolean contains(String uuid) throws Exception{
		return content.get(uuid) != null;
	}
}
