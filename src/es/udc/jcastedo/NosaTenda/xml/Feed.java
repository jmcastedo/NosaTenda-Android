package es.udc.jcastedo.NosaTenda.xml;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="feed")
public class Feed {

	//dudas con este atributo, por alguna razón se le atraganta si required no está a false
	@Attribute(name="xmlns", required=false)
	private String xmlns;
	
	@Element(name="title")
	private String title;
	
	@Element(name="subtitle")
	private String subtitle;
	
	@Element(name="link")
	private String link;
	
	@ElementList(inline=true)
	private List<Entry> list;

	
	
	public String getXmlns() {
		return xmlns;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getLink() {
		return link;
	}

	public List<Entry> getList() {
		return list;
	}
	
	
}
