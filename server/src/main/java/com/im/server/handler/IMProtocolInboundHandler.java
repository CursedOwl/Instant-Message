package com.im.server.handler;

import com.im.server.common.LoginConstants;
import com.im.server.entity.IMProtocol;
import com.im.server.factory.ProtocolFactory;
import com.im.server.message.ConnectionCallback;
import com.im.server.message.ConnectionRequest;
import com.im.server.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.im.server.common.ProtocolConstants.*;



@Slf4j
public class IMProtocolInboundHandler extends SimpleChannelInboundHandler<IMProtocol<Object>> {

    private final UserService userService;

    public IMProtocolInboundHandler(UserService userService){
        this.userService = userService;
}
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMProtocol<Object> msg) throws Exception {
        if (msg.getStatus()==FAIL_STATUS) {
            ctx.writeAndFlush(msg);
            return;
        }
       switch (msg.getCommand()){
           case CONNECTION_COMMAND:{
               ConnectionRequest connectionRequest = (ConnectionRequest) msg.getBody();
               if(!userService.account(connectionRequest.getAccount())){
                   ConnectionCallback cb = ConnectionCallback.fail("account not exist", LoginConstants.ACCOUNT_NOT_FOUND);
                   IMProtocol<Object> protocol = ProtocolFactory.createProtocol(cb, AES_ENCRYPT, PROTOBUF_ALGORITHM, CONNECTION_COMMAND, FAIL_STATUS, OBJECT_TYPE);
                   ctx.writeAndFlush(protocol);
               }
           }
       }
    }
}
