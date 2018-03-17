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



import mcc.genius.transport.api.server.JAcceptor;

import javax.imageio.spi.ServiceRegistry;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * The jupiter rpc server.
 *
 * 注意 JServer 单例即可, 不要创建多个实例.
 *
 * jupiter
 * org.jupiter.rpc
 *
 * @author jiachun.fjc
 */
public interface JServer  {



    interface ProviderInitializer<T> {

        /**
         * 初始化指定服务提供者.
         */
        void init(T provider);
    }

    /**
     * 网络层acceptor.
     */
    JAcceptor acceptor();

    /**
     * 设置网络层acceptor.
     */
    JServer withAcceptor(JAcceptor acceptor);


    /**
     * 启动jupiter server, 以同步阻塞的方式启动.
     */
    void start() throws InterruptedException;

    /**
     * 启动jupiter server, 可通过参数指定异步/同步的方式启动.
     */
    void start(boolean sync) throws InterruptedException;

    /**
     * 优雅关闭jupiter server.
     */
    void shutdownGracefully();
}
