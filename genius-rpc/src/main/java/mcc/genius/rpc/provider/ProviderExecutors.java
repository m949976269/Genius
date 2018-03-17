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


import mcc.genius.common.util.JServiceLoader;
import mcc.genius.common.util.Strings;
import mcc.genius.common.util.SystemPropertyUtil;
import mcc.genius.rpc.executor.CloseableExecutor;
import mcc.genius.rpc.executor.ExecutorFactory;

/**
 * jupiter
 * org.jupiter.rpc.provider.processor
 *
 * @author jiachun.fjc
 */
public class ProviderExecutors {

    private static final CloseableExecutor executor;

    static {
        String factoryName = SystemPropertyUtil.get("jupiter.executor.factory.provider.factory_name");
        ExecutorFactory factory;
        if (Strings.isNullOrEmpty(factoryName)) {
            factory = (ExecutorFactory) JServiceLoader.load(ProviderExecutorFactory.class)
                    .first();
        } else {
            factory = (ExecutorFactory) JServiceLoader.load(ProviderExecutorFactory.class)
                    .find(factoryName);
        }

        executor = factory.newExecutor(ExecutorFactory.Target.PROVIDER, "jupiter-provider-processor");
    }

    public static CloseableExecutor executor() {
        return executor;
    }

    public static void execute(Runnable r) {
        executor.execute(r);
    }
}
