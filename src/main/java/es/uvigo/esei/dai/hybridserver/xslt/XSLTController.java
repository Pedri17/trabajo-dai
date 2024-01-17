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

	public String getContent(String uuid) throws Exception{
		return content.getContent(uuid);
	}

	public List<String> list() throws Exception{
		return content.list();
	}

	public void delete(String uuid) throws Exception{
		content.delete(uuid);
	}

	public String getXsd(String uuid) throws Exception{
		return content.getXsd(uuid);
	}

	public boolean contains(String uuid) throws Exception{
		return content.getContent(uuid) != null;
	}
}
