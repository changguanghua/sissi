package com.sissi.pipeline.in.presence.roster;

import com.sissi.context.JID;
import com.sissi.pipeline.in.ProxyProcessor;
import com.sissi.protocol.Protocol;
import com.sissi.protocol.ProtocolType;
import com.sissi.protocol.iq.IQ;
import com.sissi.protocol.iq.roster.GroupItem;
import com.sissi.protocol.iq.roster.Roster;
import com.sissi.ucenter.Relation;
import com.sissi.ucenter.RelationRoster;

/**
 * @author kim 2014年1月16日
 */
abstract class PresenceRoster2ItemProcessor extends ProxyProcessor {

	protected Protocol prepare(JID master, JID slave) {
		Relation relation = super.ourRelation(master, slave);
		return new IQ().add(new Roster(new GroupItem(slave.asStringWithBare(), relation.getName(), super.ourRelation(master, slave).getSubscription(), RelationRoster.class.cast(relation).asGroups()))).setType(ProtocolType.SET);
	}
}
