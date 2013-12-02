package com.sissi.pipeline.in.presence.roster;

import com.sissi.context.JIDContext;
import com.sissi.pipeline.in.UtilProcessor;
import com.sissi.protocol.Protocol;
import com.sissi.protocol.presence.Presence;
import com.sissi.protocol.presence.Presence.Type;

/**
 * @author kim 2013-11-18
 */
public class PresenceRosterSubscribeProcessor extends UtilProcessor {

	@Override
	public Boolean input(JIDContext context, Protocol protocol) {
		super.presenceQueue.offer(super.jidBuilder.build(protocol.getTo()), context.getJid(), super.jidBuilder.build(protocol.getTo()), Presence.class.cast(protocol).setFrom(context.getJid()).setType(Type.SUBSCRIBE));
		return false;
	}
}
