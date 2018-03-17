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

package mcc.genius.rpc.provider;

import mcc.genius.rpc.JRequest;
import mcc.genius.rpc.JServer;
import mcc.genius.rpc.executor.CloseableExecutor;
import mcc.genius.rpc.provider.task.MessageTask;
import mcc.genius.transport.api.channel.JChannel;
import mcc.genius.transport.api.payload.JRequestPayload;


/**
 * jupiter
 * org.jupiter.rpc.provider.processor
 *
 * @author jiachun.fjc
 */
public class DefaultProviderProcessor extends AbstractProviderProcessor {

    private final CloseableExecutor executor;

    public DefaultProviderProcessor() {
        this( ProviderExecutors.executor());
    }

    //自己指定执行线程池
    public DefaultProviderProcessor(CloseableExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void handleRequest(JChannel channel, JRequestPayload requestPayload) throws Exception {
        MessageTask task = new MessageTask(this, channel, new JRequest(requestPayload));
        if (executor == null) {
            task.run();
        } else {
            executor.execute(task);
        }
    }

    @Override
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
