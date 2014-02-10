package com.sissi.pipeline.in.iq.last;

import com.sissi.context.JIDContext;
import com.sissi.context.StatusClauses;
import com.sissi.pipeline.in.ProxyProcessor;
import com.sissi.protocol.Protocol;
import com.sissi.protocol.ProtocolType;
import com.sissi.protocol.iq.last.Last;

/**
 * @author kim 2014年2月9日
 */
public class LastContacterProcessor extends ProxyProcessor {

	@Override
	public boolean input(JIDContext context, Protocol protocol) {
		JIDContext to = super.findOne(super.build(protocol.getParent().getTo()), true);
		context.write(Last.class.cast(protocol).getSeconds().seconds(to.idle()).setText(to.status().clauses().find(StatusClauses.KEY_STATUS)).getParent().setFrom(to.jid()).setType(ProtocolType.RESULT));
		return false;
	}
}
