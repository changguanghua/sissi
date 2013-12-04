package com.sissi.protocol.iq.bind;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.sissi.protocol.Protocol;
import com.sissi.protocol.Stream;
import com.sissi.read.Collector;

/**
 * @author Kim.shen 2013-10-20
 */
@XmlType(namespace = Stream.NAMESPACE)
@XmlRootElement
public class Bind extends Protocol implements Collector {

	private final static String XMLNS = "urn:ietf:params:xml:ns:xmpp-bind";

	private String jid;

	private Resource resource;

	public Bind() {
		super();
	}

	public Bind(String resource) {
		super();
		this.resource = new Resource(resource);
	}

	@XmlElement(name = "jid")
	public String getJid() {
		return jid;
	}

	public Bind setJid(String jid) {
		this.jid = jid;
		return this;
	}

	public Resource getResource() {
		return resource;
	}

	public Bind setResource(Resource resource) {
		this.resource = resource;
		return this;
	}

	@XmlAttribute
	public String getXmlns() {
		return XMLNS;
	}

	public Boolean hasResource() {
		return this.resource != null && this.resource.hasResource();
	}

	public Bind clear() {
		super.clear();
		this.jid = null;
		this.resource = null;
		return this;
	}

	@Override
	public void set(String localName, Object ob) {
		this.setResource((Resource) ob);
	}
}