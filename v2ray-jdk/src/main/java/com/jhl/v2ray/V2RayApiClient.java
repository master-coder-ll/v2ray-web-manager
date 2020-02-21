package com.jhl.v2ray;


import com.v2ray.core.app.log.command.LoggerServiceGrpc;
import com.v2ray.core.app.proxyman.command.HandlerServiceGrpc;
import com.v2ray.core.app.stats.command.StatsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Data
@AllArgsConstructor
public class V2RayApiClient {

    private HandlerServiceGrpc.HandlerServiceBlockingStub handlerServiceBlockingStub;
    private LoggerServiceGrpc.LoggerServiceBlockingStub loggerServiceBlockingStub;
    private StatsServiceGrpc.StatsServiceBlockingStub statsServiceBlockingStub;
    private static final ConcurrentHashMap<String, V2RayApiClient> concurrentHashMap = new ConcurrentHashMap();
    private static final ExecutorService SINGLE_POLL = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "grpc_single_thread");
        }
    });


    public static V2RayApiClient getInstance(String host, int port) {
        String key = host + port;
        if (concurrentHashMap.containsKey(key)) return concurrentHashMap.get(key);
        synchronized (key.intern()) {
            if (!concurrentHashMap.containsKey(key)) {
                ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).executor(SINGLE_POLL)
                        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                        // needing certificates.
                        .usePlaintext()
                        .build();
                V2RayApiClient v2RayApiClient1 = new V2RayApiClient(HandlerServiceGrpc.newBlockingStub(channel),
                        LoggerServiceGrpc.newBlockingStub(channel),
                        StatsServiceGrpc.newBlockingStub(channel));

                concurrentHashMap.put(key, v2RayApiClient1);
            }
            return concurrentHashMap.get(key);
        }

    }


    public void shutdown() throws InterruptedException {
        // concurrentHashMap.forEachValue();
        //channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


}
