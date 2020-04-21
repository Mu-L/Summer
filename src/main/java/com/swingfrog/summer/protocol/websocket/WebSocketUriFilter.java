package com.swingfrog.summer.protocol.websocket;

import com.swingfrog.summer.server.exception.WebSocketUriNoFoundException;

import com.swingfrog.summer.util.ForwardedAddressUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;

public class WebSocketUriFilter extends SimpleChannelInboundHandler<FullHttpRequest> {

	private String wsUri;
	 
    public WebSocketUriFilter(String wsUri) {
        this.wsUri = wsUri;
    }
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if (wsUri.equalsIgnoreCase(request.uri())) {
			ctx.channel().attr(AttributeKey.valueOf(ForwardedAddressUtil.KEY)).set(request.headers().get(ForwardedAddressUtil.KEY));
            ctx.fireChannelRead(request.retain());
        } else {
        	ctx.close();
        	throw new WebSocketUriNoFoundException(request.uri());
        }
	}

}
