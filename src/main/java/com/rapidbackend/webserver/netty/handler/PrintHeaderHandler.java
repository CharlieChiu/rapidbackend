package com.rapidbackend.webserver.netty.handler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

/**
 * One handler which only prints the incoming request's header back to the requester
 * @author chiqiu
 *
 */
public class PrintHeaderHandler extends SimpleChannelUpstreamHandler{
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object event = e.getMessage();
        if(event instanceof HttpRequest){
            HttpRequest request = (HttpRequest)event;
            StringBuffer sb = new StringBuffer("");
            sb.append("uri:").append(request.getUri());
            sb.append("\r\n");
            for(Map.Entry<String, String> entry: request.getHeaders()){
                sb.append(entry.getKey()).append(':').append(entry.getValue());
                sb.append("\r\n");
            }
            ChannelBuffer buf = ChannelBuffers.copiedBuffer(sb
                    .toString(), CharsetUtil.UTF_8);
            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK);
            Set<Cookie> cookies;
            String value = request.getHeader(HttpHeaders.Names.COOKIE);
            if (value == null) {
                cookies = Collections.emptySet();
            } else {
                CookieDecoder decoder = new CookieDecoder();
                cookies = decoder.decode(value);
            }
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                CookieEncoder cookieEncoder = new CookieEncoder(true);
                for (Cookie cookie: cookies) {
                    cookieEncoder.addCookie(cookie);
                    response.addHeader(HttpHeaders.Names.SET_COOKIE, cookieEncoder
                            .encode());
                    cookieEncoder = new CookieEncoder(true);
                }
            }
            boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(request
                    .getHeader(HttpHeaders.Names.CONNECTION)) ||
                    request.getProtocolVersion().equals(HttpVersion.HTTP_1_0) &&
                    !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(request
                            .getHeader(HttpHeaders.Names.CONNECTION));
            if (!close) {
                // There's no need to add 'Content-Length' header
                // if this is the last response.
                response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String
                        .valueOf(buf.readableBytes()));
            }
            
            response.setContent(buf);
            ChannelFuture future = ctx.getChannel().write(response);
            // Close the connection after the write operation is done if necessary.
            if (close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            
        }
    }
}
