package com.snowcattle.game.service.net.websocket;

import com.snowcattle.game.bootstrap.manager.LocalMananger;
import com.snowcattle.game.common.config.GameServerConfig;
import com.snowcattle.game.common.constant.GlobalConstants;
import com.snowcattle.game.service.config.GameServerConfigService;
import com.snowcattle.game.service.net.tcp.handler.GameLoggingHandler;
import com.snowcattle.game.service.net.websocket.handler.async.AsyncNettyWebSocketHandlerService;
import com.snowcattle.game.service.net.websocket.handler.async.AsyncWebSocketFrameServerHandler;
import com.snowcattle.game.service.net.websocket.handler.WebSocketServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;

/**
 * Created by jiangwenping on 2017/11/8.
 */
public class GameNetProtoMessageWebSocketServerChannelInitializer  extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline channelPipLine = socketChannel.pipeline();
        channelPipLine.addLast("encoder", new HttpResponseEncoder());
        channelPipLine.addLast("decoder", new HttpRequestDecoder());
//        channelPipLine.addLast("codec" , new HttpServerCodec());
        channelPipLine.addLast(new HttpObjectAggregator(65536));
        GameServerConfigService gameServerConfigService = LocalMananger.getInstance().getLocalSpringServiceManager().getGameServerConfigService();
        GameServerConfig gameServerConfig = gameServerConfigService.getGameServerConfig();
        if(gameServerConfig.isDevelopModel()) {
            channelPipLine.addLast("logger", new GameLoggingHandler(LogLevel.DEBUG));
        }

        AsyncNettyWebSocketHandlerService asyncNettyWebSocketHandlerService = LocalMananger.getInstance().getLocalSpringServiceManager().getAsyncNettyWebSocketHandlerService();
        channelPipLine.addLast(asyncNettyWebSocketHandlerService.getDefaultEventExecutorGroup(), GlobalConstants.ChannelPipeline.WebSocketServerHandler, new WebSocketServerHandler());
        channelPipLine.addLast(asyncNettyWebSocketHandlerService.getDefaultEventExecutorGroup(), GlobalConstants.ChannelPipeline.WebSocketFrameServerHandler, new AsyncWebSocketFrameServerHandler());
    }
}
