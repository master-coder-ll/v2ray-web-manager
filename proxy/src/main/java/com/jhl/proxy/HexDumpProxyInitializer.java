package com.jhl.proxy;/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import com.jhl.TrafficController.TrafficController;
import com.jhl.cache.ProxyAccountCache;
import com.jhl.config.ProxyConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HexDumpProxyInitializer extends ChannelInitializer<SocketChannel> {
     final  ProxyConfig proxyConfig;
    final  TrafficController trafficController;
    final ProxyAccountCache proxyAccountCache;
    @Override
    public void initChannel(SocketChannel ch) {

        ch.pipeline().addLast(
             //   new ChannelTrafficShapingHandler(1024*1024,0),
                new HexDumpProxyFrontendHandler(proxyConfig,trafficController,proxyAccountCache));
    }
}
