package com.sissi.pipeline.in.presence.muc;

import com.sissi.context.JID;
import com.sissi.context.JIDContext;
import com.sissi.pipeline.in.ProxyProcessor;
import com.sissi.protocol.Protocol;
import com.sissi.ucenter.RelationMuc;
import com.sissi.ucenter.relation.PresenceMucWrapRelation;

/**
 * @author kim 2014年2月11日
 */
public class PresenceMucJoinProcessor extends ProxyProcessor {

	@Override
	public boolean input(JIDContext context, Protocol protocol) {
		JID group = super.build(protocol.getTo());
		System.out.println(group.asString());
		System.out.println(context.jid().asString());
		super.establish(context.jid(), new PresenceMucWrapRelation(group, RelationMuc.class.cast(super.ourRelation(context.jid(), group))));
		return true;
	}
}
