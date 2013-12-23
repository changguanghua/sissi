package com.sissi.addressing.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sissi.addressing.Addressing;
import com.sissi.commons.Interval;
import com.sissi.commons.Runner;
import com.sissi.config.MongoConfig;
import com.sissi.context.JID;
import com.sissi.context.JIDContext;
import com.sissi.context.JIDContextBuilder;
import com.sissi.context.JIDContextParam;
import com.sissi.context.impl.JIDContexts;

/**
 * @author kim 2013-11-1
 */
public class MongoAddressing implements Addressing {

	private final Integer GC_THREAD = 1;

	private final String FIELD_INDEX = "index";

	private final String FIELD_PRIORITY = "priority";

	private final DBObject DEFAULT_FILTER = BasicDBObjectBuilder.start(FIELD_INDEX, 1).get();

	private final DBObject DEFAULT_SORTER = BasicDBObjectBuilder.start(FIELD_PRIORITY, 1).get();

	private final JIDContextParam NOTHING = new NothingJIDContextParam();

	private final Log log = LogFactory.getLog(this.getClass());

	private final Map<Long, JIDContext> contexts = new ConcurrentHashMap<Long, JIDContext>();

	private final MongoConfig config;

	private final JIDContextBuilder contextBuilder;

	public MongoAddressing(Runner runner, Interval interval, MongoConfig config, JIDContextBuilder contextBuilder) {
		super();
		this.config = config.clear();
		this.contextBuilder = contextBuilder;
		runner.executor(GC_THREAD, new GC(interval));
	}

	@Override
	public Addressing join(JIDContext context) {
		this.contexts.put(context.getIndex(), context);
		this.config.collection().save(this.buildQueryWithFullFields(context));
		return this;
	}

	public Addressing leave(JID jid) {
		for (JIDContext current : this.find(jid, true, false)) {
			this.leave(current);
		}
		return this;
	}

	@Override
	public Addressing leave(JIDContext context) {
		if (context.close()) {
			this.contexts.remove(context.getIndex());
			this.config.collection().remove(this.buildQueryWithFullFields(context));
		}
		return this;
	}

	public Integer others(JID jid) {
		return this.find(jid, false, false).size();
	}

	@Override
	public JIDContexts find(JID jid) {
		return this.find(jid, false);
	}

	@Override
	public JIDContexts find(JID jid, Boolean usingResource) {
		return this.find(jid, usingResource, true);
	}

	@Override
	public JIDContext findOne(JID jid) {
		DBObject entity = this.config.collection().findOne(this.buildQueryWithSmartResource(jid, false), DEFAULT_FILTER);
		return entity != null ? this.contexts.get(Long.class.cast(entity.get("index"))) : this.contextBuilder.build(jid, NOTHING);
	}

	private JIDContexts find(JID jid, Boolean usingResource, Boolean usingOffline) {
		return new MongoUserContexts(jid, usingOffline, this.config.collection().find(this.buildQueryWithSmartResource(jid, usingResource), DEFAULT_FILTER).sort(DEFAULT_SORTER));
	}

	private DBObject buildQueryWithFullFields(JIDContext context) {
		DBObject query = BasicDBObjectBuilder.start().add("jid", context.getJid().asStringWithBare()).add("resource", context.getJid().getResource()).add(FIELD_PRIORITY, context.getPriority()).add("index", context.getIndex()).get();
		this.log.debug("Query: " + query);
		return query;
	}

	private DBObject buildQueryWithSmartResource(JID jid, Boolean usingResource) {
		DBObject query = BasicDBObjectBuilder.start().add("jid", jid.asStringWithBare()).get();
		if (usingResource && jid.getResource() != null) {
			query.put("resource", jid.getResource());
		}
		this.log.debug("Query: " + query);
		return query;
	}

	private boolean exists(Long index) {
		return this.config.collection().findOne(BasicDBObjectBuilder.start().add("index", index).get(), DEFAULT_FILTER) != null;
	}

	private class MongoUserContexts extends JIDContexts {

		private final static long serialVersionUID = 1L;

		private MongoUserContexts(JID jid, Boolean usingOffline, DBCursor cursor) {
			while (cursor.hasNext()) {
				JIDContext context = MongoAddressing.this.contexts.get(Long.class.cast(cursor.next().get("index")));
				if (context != null) {
					this.add(context);
				}
			}
			this.usingOffline(jid, usingOffline);
		}

		private void usingOffline(JID jid, Boolean usingOffline) {
			if (usingOffline && this.isEmpty()) {
				this.add(MongoAddressing.this.contextBuilder.build(jid, NOTHING));
			}
		}
	}

	private class GC implements Runnable {

		private final Long sleep;

		public GC(Interval interval) {
			super();
			this.sleep = TimeUnit.MILLISECONDS.convert(interval.getInterval(), interval.getUnit());
		}

		@Override
		public void run() {
			while (true) {
				try {
					this.gc();
					Thread.sleep(this.sleep);
				} catch (Exception e) {
					MongoAddressing.this.log.error(e);
				}
			}
		}

		public void gc() {
			for (Long index : MongoAddressing.this.contexts.keySet()) {
				if (!MongoAddressing.this.exists(index)) {
					JIDContext leak = MongoAddressing.this.contexts.get(index);
					MongoAddressing.this.leave(leak);
					MongoAddressing.this.log.error("Find leak context: " + leak.getJid().asString());
				}
			}
		}
	}

	private class NothingJIDContextParam implements JIDContextParam {

		private NothingJIDContextParam() {
		}

		@Override
		public <T> T find(String key, Class<T> clazz) {
			return null;
		}
	}
}
