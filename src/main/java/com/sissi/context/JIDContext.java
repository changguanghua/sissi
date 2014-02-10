package com.sissi.context;

import java.net.SocketAddress;
import java.util.Collection;

import com.sissi.protocol.Element;

/**
 * @author kim 2013-10-27
 */
public interface JIDContext {

	public long index();

	public JIDContext jid(JID jid);

	public JID jid();

	public JIDContext auth(boolean canAccess);

	public boolean auth();

	public boolean authRetry();

	public JIDContext bind();

	public boolean binding();

	public JIDContext priority(int priority);

	public int priority();

	public JIDContext domain(String domain);

	public String domain();

	public JIDContext lang(String lang);

	public String lang();

	public SocketAddress address();

	public boolean encrypt();

	public boolean encrypted();

	public JIDContext present();

	public boolean presented();
	
	public long idle();

	public Status status();

	public JIDContext reset();

	public boolean close();

	public boolean closePrepare();

	public boolean closeTimeout();

	public JIDContext ping();

	public JIDContext pong(Element element);

	public JIDContext write(Element element);

	public JIDContext write(Collection<Element> elements);
}
