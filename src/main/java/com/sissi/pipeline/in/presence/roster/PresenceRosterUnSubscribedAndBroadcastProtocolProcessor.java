package com.sissi.pipeline.in.presence.roster;

import com.sissi.context.JID;
import com.sissi.context.JIDContext;
import com.sissi.pipeline.in.UtilProcessor;
import com.sissi.protocol.Protocol;
import com.sissi.protocol.Protocol.Type;
import com.sissi.protocol.iq.IQ;
import com.sissi.protocol.iq.roster.Item;
import com.sissi.protocol.iq.roster.Roster;
import com.sissi.relation.Relation;
import com.sissi.relation.RelationRoster;

/**
 * @author kim 2013-11-18
 */
public class PresenceRosterUnSubscribedAndBroadcastProtocolProcessor extends UtilProcessor {

	@Override
	public Boolean input(JIDContext context, Protocol protocol) {
		JID master = super.jidBuilder.build(protocol.getTo());
		super.protocolQueue.offer(master, this.prepareResponse(master, context.getJid(), protocol));
		return true;
	}

	private Protocol prepareResponse(JID master, JID slave, Protocol protocol) {
		Relation relation = super.relationContext.ourRelation(master, slave);
		return ((IQ) new IQ(Type.SET).setTo(master)).add(new Roster().add(new Item(slave.asStringWithBare(), relation.getName(), Roster.Subscription.NONE.toString(), RelationRoster.class.cast(relation).getGroupText())));
	}
}
