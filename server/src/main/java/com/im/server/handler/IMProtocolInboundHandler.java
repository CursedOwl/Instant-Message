package com.im.server.handler;

import com.im.server.common.KafkaConstants;
import com.im.server.common.LoginConstants;
import com.im.server.message.IMProtocol;
import com.im.server.factory.ProtocolFactory;
import com.im.server.message.ConnectionCallback;
import com.im.server.message.ConnectionRequest;
import com.im.server.message.PublishMessage;
import com.im.server.service.GroupService;
import com.im.server.service.KafkaService;
import com.im.server.service.MessageService;
import com.im.server.service.UserService;
import com.im.server.util.TokenUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.im.server.common.ProtocolConstants.*;
import static com.im.server.common.RedisConstants.*;



@Slf4j
public class IMProtocolInboundHandler extends SimpleChannelInboundHandler<IMProtocol<Object>> {

    private final UserService userService;

    private final MessageService messageService;

    private final KafkaService kafkaService;

    private final StringRedisTemplate redisTemplate;

    private final GroupService groupService;

    private final ConcurrentHashMap<Integer, Channel> accountWithChannel;

    private final String secret;

    public IMProtocolInboundHandler(String secret,ConcurrentHashMap<Integer,Channel> accountWithChannel,
                                    UserService userService,MessageService messageService,StringRedisTemplate redisTemplate,
                                    KafkaService kafkaService,GroupService groupService){
        this.userService = userService;
        this.secret=secret;
        this.accountWithChannel=accountWithChannel;
        this.messageService=messageService;
        this.kafkaService=kafkaService;
        this.redisTemplate=redisTemplate;
        this.groupService=groupService;
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
                       IMProtocol<Object> protocol = ProtocolFactory.createProtocol(cb, NO_ENCRYPT, JSON_ALGORITHM, CONNECTION_COMMAND, FAIL_STATUS, OBJECT_TYPE);
                       ctx.writeAndFlush(protocol);
                       return;
                   }
                   if(!userService.login(connectionRequest.getAccount(),connectionRequest.getPassword())){
                       ConnectionCallback cb = ConnectionCallback.fail("wrong password", LoginConstants.PASSWORD_ERROR);
                       IMProtocol<Object> protocol = ProtocolFactory.createProtocol(cb, NO_ENCRYPT, JSON_ALGORITHM, CONNECTION_COMMAND, FAIL_STATUS, OBJECT_TYPE);
                       ctx.writeAndFlush(protocol);
                       return;
                   }
                   String token = TokenUtil.create(secret, connectionRequest.getAccount());
                   ConnectionCallback cb = ConnectionCallback.success(token, LoginConstants.SUCCESS);
                   IMProtocol<Object> protocol = ProtocolFactory.createProtocol(
                           cb, NO_ENCRYPT, JSON_ALGORITHM,
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
               kafkaService.sendPublish(KafkaConstants.MSG_TOPIC,publish);
//               私聊消息和群聊消息的处理方式不一样，但是最终都要存入ES中
               if (publish.getPrivate()){
//                   TODO 单机节点直接从Map中查询，而分布式节点未找到存空数据到Redis防止穿透
                   Channel toChannel;
                   if(!accountWithChannel.containsKey(to)){
                       break;
                   }
                   if ((toChannel=accountWithChannel.get(to))!=null) {
                       IMProtocol<PublishMessage> simpleProtocol = ProtocolFactory.createSimpleProtocol(publish);
//                   用户在线直接写入，而不管在线与否都存入ES中，拉取离线消息则用时间戳去查询ES
                       toChannel.writeAndFlush(simpleProtocol);
                   }else {
//                       懒删除，当有人发送消息再删除
                       accountWithChannel.remove(to);
                   }
               }else {
                   List<Integer> members;
//                   看Redis中是否有，如果没有则从数据库查一份写入，记得加TTL
                   if(Boolean.FALSE.equals(redisTemplate.hasKey(MEMBERS_KEY_PREFIX))){
                       members=groupService.getMembers(to);
                       redisTemplate.opsForSet().add(MEMBERS_KEY_PREFIX+to,members.stream().map(Object::toString).toArray(String[]::new));
                       redisTemplate.expire(MEMBERS_KEY_PREFIX+to, Duration.ofMinutes(60));
                   }else {
                       Set<String> getMembers = redisTemplate.opsForSet().members(MEMBERS_KEY_PREFIX + to);
                       assert getMembers!=null;
                       members=getMembers.stream().map(Integer::parseInt).collect(Collectors.toList());
                   }

                   members.forEach(member->{
                       if(accountWithChannel.containsKey(member)){
                           Channel channel = accountWithChannel.get(member);
                           channel.writeAndFlush(ProtocolFactory.createSimpleProtocol(publish));
                       }
                   });

               }
               break;
           }

       }
    }
}
