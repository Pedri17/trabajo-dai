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

	public List<String> list() throws Exception{
		try{
			return content.list();
		}catch(Exception e){
			throw e;
		}
	}

	public void delete(String uuid) throws Exception {
		try{
			content.delete(uuid);
		}catch(Exception e){
			throw e;
		}
	}

	public String getContent(String uuid) throws Exception {
		try{
			return content.get(uuid);
		}catch(Exception e){
			throw e;
		}
	}

	public boolean contains(String uuid) throws Exception {
		try{
			return content.get(uuid) != null;
		}catch(Exception e){
			throw e;
		}
	}
}
