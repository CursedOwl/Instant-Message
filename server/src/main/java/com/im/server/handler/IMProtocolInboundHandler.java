package com.im.server.handler;

import cn.hutool.core.util.StrUtil;
import com.im.server.common.LoginConstants;
import com.im.server.common.ProtocolConstants;
import com.im.server.entity.IMProtocol;
import com.im.server.factory.ProtocolFactory;
import com.im.server.message.ConnectionCallback;
import com.im.server.message.ConnectionRequest;
import com.im.server.message.PublishMessage;
import com.im.server.service.MessageService;
import com.im.server.service.UserService;
import com.im.server.util.TokenUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.ibatis.annotations.Case;

import java.util.concurrent.ConcurrentHashMap;

import static com.im.server.common.ProtocolConstants.*;



@Slf4j
public class IMProtocolInboundHandler extends SimpleChannelInboundHandler<IMProtocol<Object>> {

    private final UserService userService;

    private final MessageService messageService;

    private final ConcurrentHashMap<Integer, Channel> accountWithChannel;
    private final String secret;

    public IMProtocolInboundHandler(String secret,ConcurrentHashMap<Integer,Channel> accountWithChannel,
                                    UserService userService,MessageService messageService){
        this.userService = userService;
        this.secret=secret;
        this.accountWithChannel=accountWithChannel;
        this.messageService=messageService;
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
//               CONNECTION_FIRST附带认证信息
               if(msg.getStatus()==CONNECTION_FIRST){
                   if(!userService.account(connectionRequest.getAccount())){
                       ConnectionCallback cb = ConnectionCallback.fail("account not exist", LoginConstants.ACCOUNT_NOT_FOUND);
                       IMProtocol<Object> protocol = ProtocolFactory.createProtocol(cb, AES_ENCRYPT, PROTOBUF_ALGORITHM, CONNECTION_COMMAND, FAIL_STATUS, OBJECT_TYPE);
                       ctx.writeAndFlush(protocol);
                       return;
                   }
                   if(!userService.login(connectionRequest.getAccount(),connectionRequest.getPassword())){
                       ConnectionCallback cb = ConnectionCallback.fail("wrong password", LoginConstants.PASSWORD_ERROR);
                       IMProtocol<Object> protocol = ProtocolFactory.createProtocol(cb, AES_ENCRYPT, PROTOBUF_ALGORITHM, CONNECTION_COMMAND, FAIL_STATUS, OBJECT_TYPE);
                       ctx.writeAndFlush(protocol);
                       return;
                   }
                   String token = TokenUtil.create(secret, connectionRequest.getAccount());
                   ConnectionCallback cb = ConnectionCallback.success(token, LoginConstants.SUCCESS);
                   IMProtocol<Object> protocol = ProtocolFactory.createProtocol(
                           cb, AES_ENCRYPT, PROTOBUF_ALGORITHM,
                           CONNECTION_COMMAND, CONNECTION_SECOND, OBJECT_TYPE);
                   ctx.writeAndFlush(protocol);
                   return;
//                   CONNECTION_THIRD携带SECOND附带第一次发送的的JWT，如果JWT解析失败一定是出异常
               }else if(msg.getStatus()==CONNECTION_THIRD){
                   String jwt = connectionRequest.getJwt();
                   Tuple2<Boolean, String> verify = TokenUtil.verify(jwt);
                   if(!verify.f0||!verify.f1.equals(connectionRequest.getAccount())){
                       log.error("Token verify failed");
                       ctx.close();
                       return;
                   }
                   accountWithChannel.put(Integer.parseInt(verify.f1), ctx.channel());
;               }
               break;
           }

           case PUBLISH_COMMAND:{
               PublishMessage publish = (PublishMessage) msg.getBody();
               Integer to = publish.getTo();
               Channel toChannel;
               if ((toChannel=accountWithChannel.get(to))!=null) {
                   IMProtocol<PublishMessage> simpleProtocol = ProtocolFactory.createSimpleProtocol(publish);
                   toChannel.writeAndFlush(simpleProtocol);
               }

               break;
           }
       }
    }
}
