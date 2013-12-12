package com.sissi.ucenter;

import java.util.Map;

/**
 * @author kim 2013-11-13
 */
public interface Relation {

	public String getJID();

	public String getName();

	public String getSubscription();

	public Boolean isBan();

	public Map<String, Object> plus();

	public interface RelationRoster extends Relation {

		public String asGroup();
	}
}
