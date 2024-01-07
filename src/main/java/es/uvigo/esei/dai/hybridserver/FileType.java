package es.uvigo.esei.dai.hybridserver;

public enum FileType {
    HTML("html"),
    XML("xml"),
    XSLT("xslt"),
    XSD("xsd");

    private String type;

    private FileType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
