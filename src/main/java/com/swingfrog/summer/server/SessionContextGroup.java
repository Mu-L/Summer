package com.swingfrog.summer.server;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.swingfrog.summer.util.ForwardedAddressUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public class SessionContextGroup {

	private ConcurrentHashMap<ChannelHandlerContext, SessionContext> channelToSessionMap;
	private ConcurrentHashMap<SessionContext, ChannelHandlerContext> sessionToChannelMap;
	
	public SessionContextGroup() {
		channelToSessionMap = new ConcurrentHashMap<>();
		sessionToChannelMap = new ConcurrentHashMap<>();
	}
	
	public void createSession(ChannelHandlerContext ctx) {
		String id = ctx.channel().id().asLongText();
		InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
		SessionContext sctx = new SessionContext();
		sctx.setSessionId(id);
		sctx.setDirectAddress(address.getHostString());
		Object forwardedAddressList = ctx.channel().attr(AttributeKey.valueOf(ForwardedAddressUtil.KEY)).get();
		if (forwardedAddressList != null)
			sctx.setRealAddress(ForwardedAddressUtil.parse(forwardedAddressList.toString()));
		sctx.setPort(address.getPort());
		sctx.setCurrentMsgId(0);
		sctx.setHeartCount(0);
		sctx.setLastRecvTime(0);
		channelToSessionMap.put(ctx, sctx);
		sessionToChannelMap.put(sctx, ctx);
	}
	
	public void destroySession(ChannelHandlerContext ctx) {
		sessionToChannelMap.remove(channelToSessionMap.remove(ctx));
	}
	
	public SessionContext getSessionByChannel(ChannelHandlerContext ctx) {
		return channelToSessionMap.get(ctx);
	}
	
	public ChannelHandlerContext getChannelBySession(SessionContext sctx) {
		return sessionToChannelMap.get(sctx);
	}
	
	public Iterator<SessionContext> iteratorSession() {
		return sessionToChannelMap.keySet().iterator();
	}
	
	public Iterator<ChannelHandlerContext> iteratorChannel() {
		return channelToSessionMap.keySet().iterator();
	}
	
}
