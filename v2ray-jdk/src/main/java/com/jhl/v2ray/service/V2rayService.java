package com.jhl.v2ray.service;



import com.google.gson.Gson;
import com.jhl.v2ray.V2RayApiClient;
import com.ljh.common.model.ProxyAccount;
import com.v2ray.core.app.proxyman.command.AddUserOperation;
import com.v2ray.core.app.proxyman.command.AlterInboundRequest;
import com.v2ray.core.app.proxyman.command.RemoveUserOperation;
import com.v2ray.core.common.protocol.SecurityConfig;
import com.v2ray.core.common.protocol.SecurityType;
import com.v2ray.core.common.protocol.User;
import com.v2ray.core.common.serial.TypedMessage;
import com.v2ray.core.proxy.vmess.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class V2rayService {


    public void rmProxyAccount(String host, Integer port, ProxyAccount proxyAccount) {
        try {
            V2RayApiClient client = V2RayApiClient.getInstance(host, port);


            TypedMessage rmOp = TypedMessage.newBuilder().setType(RemoveUserOperation.getDescriptor().getFullName())
                    .setValue(RemoveUserOperation.newBuilder().setEmail(proxyAccount.getEmail()).build().toByteString()).build();
            AlterInboundRequest req = AlterInboundRequest.newBuilder().setTag(proxyAccount.getInBoundTag()).setOperation(rmOp).build();
            client.getHandlerServiceBlockingStub().alterInbound(req);
        } catch (Exception e) {
            log.error("rmProxyAccount error:{},{}", e.getLocalizedMessage(), new Gson().toJson(proxyAccount));
        }
    }

    public void addProxyAccount(String host, Integer port, ProxyAccount proxyAccount) {
        try {
            V2RayApiClient client = V2RayApiClient.getInstance(host, port);
            Account account = Account.newBuilder().setAlterId(proxyAccount.getAlterId()).setId(proxyAccount.getId())
                    .setSecuritySettings(SecurityConfig.newBuilder().setType(SecurityType.AUTO).build()).build();

            TypedMessage AccountTypedMsg = TypedMessage.newBuilder().
                    setType(Account.getDescriptor().getFullName()
                    ).setValue(account.toByteString()).build();

            User user = User.newBuilder().setEmail(proxyAccount.getEmail()).setLevel(proxyAccount.getLevel()).setAccount(AccountTypedMsg).build();
            AddUserOperation addUserOperation = AddUserOperation.newBuilder().setUser(user).build();

            TypedMessage typedMessage = TypedMessage.newBuilder().setType(AddUserOperation.getDescriptor().getFullName())
                    .setValue(addUserOperation.toByteString()).build();

            client.getHandlerServiceBlockingStub().alterInbound(AlterInboundRequest.newBuilder().setTag(proxyAccount.getInBoundTag()).setOperation(typedMessage).build())
            ;
        } catch (Exception e) {
            log.error("addProxyAccount error:{},{}", e.getLocalizedMessage(), new Gson().toJson(proxyAccount));

        }
    }
}
