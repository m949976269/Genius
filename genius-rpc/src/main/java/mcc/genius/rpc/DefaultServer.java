/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mcc.genius.rpc;

import mcc.genius.common.util.ClassUtil;
import mcc.genius.common.util.internal.logging.InternalLogger;
import mcc.genius.common.util.internal.logging.InternalLoggerFactory;
import mcc.genius.rpc.provider.DefaultProviderProcessor;
import mcc.genius.transport.api.server.JAcceptor;

import javax.imageio.spi.ServiceRegistry;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;


/**
 * Jupiter默认服务端实现.
 *
 * jupiter
 * org.jupiter.rpc
 *
 * @author jiachun.fjc
 */
public class DefaultServer implements JServer {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultServer.class);

    static {
        // touch off TracingUtil.<clinit>
        // because getLocalAddress() and getPid() sometimes too slow
        ClassUtil.classInitialize("org.jupiter.rpc.tracing.TracingUtil", 500);
    }

    // IO acceptor
    private JAcceptor acceptor;

    public DefaultServer() {

    }

    @Override
    public JAcceptor acceptor() {
        return acceptor;
    }

    @Override
    public JServer withAcceptor(JAcceptor acceptor) {
        if (acceptor.processor() == null) {
            acceptor.withProcessor(new DefaultProviderProcessor());
        }
        this.acceptor = acceptor;
        return this;
    }




    @Override
    public void start() throws InterruptedException {
        acceptor.start();
    }

    @Override
    public void start(boolean sync) throws InterruptedException {
        acceptor.start(sync);
    }

    @Override
    public void shutdownGracefully() {

        acceptor.shutdownGracefully();
    }

    public void setAcceptor(JAcceptor acceptor) {
        withAcceptor(acceptor);
    }






}
