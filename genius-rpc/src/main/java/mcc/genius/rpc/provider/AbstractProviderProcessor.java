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

import mcc.genius.common.util.ExceptionUtil;
import mcc.genius.common.util.internal.logging.InternalLogger;
import mcc.genius.common.util.internal.logging.InternalLoggerFactory;
import mcc.genius.rpc.model.ResultWrapper;
import mcc.genius.transport.api.channel.JChannel;
import mcc.genius.transport.api.channel.JFutureListener;
import mcc.genius.transport.api.config.Status;
import mcc.genius.transport.api.payload.JRequestPayload;
import mcc.genius.transport.api.payload.JResponsePayload;
import mcc.genius.transport.api.processor.ProviderProcessor;

import static mcc.genius.common.util.StackTraceUtil.stackTrace;

/**
 * jupiter
 * org.jupiter.rpc.provider.processor
 *
 * @author jiachun.fjc
 */
public abstract class AbstractProviderProcessor implements ProviderProcessor {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractProviderProcessor.class);

    @Override
    public void handleException(JChannel channel, JRequestPayload request, Status status, Throwable cause) {
        logger.error("An exception was caught while processing request: {}, {}.",
                channel.remoteAddress(), stackTrace(cause));

        doHandleException(
                channel, request.invokeId(), request.serializerCode(), status.value(), cause, false);
    }





    private void doHandleException(
            JChannel channel, long invokeId, byte s_code, byte status, Throwable cause, boolean closeChannel) {

//        ResultWrapper result = new ResultWrapper();
//        // 截断cause, 避免客户端无法找到cause类型而无法序列化
//        cause = ExceptionUtil.cutCause(cause);
//        result.setError(cause);
//
////        Serializer serializer = SerializerFactory.getSerializer(s_code);
////        byte[] bytes = serializer.writeObject(result);
//
//        JResponsePayload response = new JResponsePayload(invokeId);
//        response.status(status);
//        response.bytes(s_code, bytes);
//
//        if (closeChannel) {
//            channel.write(response, JChannel.CLOSE);
//        } else {
//            channel.write(response, new JFutureListener<JChannel>() {
//
//                @Override
//                public void operationSuccess(JChannel channel) throws Exception {
//                    logger.debug("Service error message sent out: {}.", channel);
//                }
//
//                @Override
//                public void operationFailure(JChannel channel, Throwable cause) throws Exception {
//                    if (logger.isWarnEnabled()) {
//                        logger.warn("Service error message sent failed: {}, {}.", channel, stackTrace(cause));
//                    }
//                }
//            });
//        }
    }
}
