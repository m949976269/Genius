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

package mcc.genius.rpc.provider.task;


import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import mcc.genius.common.util.Signal;
import mcc.genius.common.util.SystemPropertyUtil;
import mcc.genius.common.util.internal.UnsafeIntegerFieldUpdater;
import mcc.genius.common.util.internal.UnsafeUpdater;
import mcc.genius.common.util.internal.logging.InternalLogger;
import mcc.genius.common.util.internal.logging.InternalLoggerFactory;
import mcc.genius.rpc.JRequest;
import mcc.genius.rpc.metric.Metrics;
import mcc.genius.rpc.model.MessageWrapper;
import mcc.genius.rpc.provider.AbstractProviderProcessor;
import mcc.genius.rpc.tracing.TraceId;
import mcc.genius.serialization.api.InputBuf;
import mcc.genius.transport.api.channel.JChannel;
import mcc.genius.transport.api.config.CodecConfig;
import mcc.genius.transport.api.payload.JRequestPayload;


/**
 *
 * jupiter
 * org.jupiter.rpc.provider.processor.task
 *
 * @author jiachun.fjc
 */
public class MessageTask implements Runnable {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MessageTask.class);

    private static final boolean METRIC_NEEDED = SystemPropertyUtil.getBoolean("jupiter.metric.needed", false);

    private static final Signal INVOKE_ERROR = Signal.valueOf(MessageTask.class, "INVOKE_ERROR");

    private static final UnsafeIntegerFieldUpdater<TraceId> traceNodeUpdater =
            UnsafeUpdater.newIntegerFieldUpdater(TraceId.class, "node");

    private final AbstractProviderProcessor processor;
    private final JChannel channel;
    private final JRequest request;

    public MessageTask(AbstractProviderProcessor processor, JChannel channel, JRequest request) {
        this.processor = processor;
        this.channel = channel;
        this.request = request;
    }

    @Override
    public void run() {
        // stack copy
        final AbstractProviderProcessor _processor = processor;
        final JRequest _request = request;


        MessageWrapper msg;
        try {
            JRequestPayload _requestPayload = _request.payload();

            byte s_code = _requestPayload.serializerCode();
//            Serializer serializer = SerializerFactory.getSerializer(s_code);

            // 在业务线程中反序列化, 减轻IO线程负担
            if (CodecConfig.isDecodeLowCopy()) {
                InputBuf inputBuf = _requestPayload.inputBuf();
//                msg = serializer.readObject(inputBuf, MessageWrapper.class);
            } else {
                byte[] bytes = _requestPayload.bytes();
//                msg = serializer.readObject(bytes, MessageWrapper.class);
            }
            _requestPayload.clear();

//            _request.message(msg);
        } catch (Throwable t) {
//            rejected(Status.BAD_REQUEST, new JupiterBadRequestException(t.getMessage()));
            return;
        }


    }











    // - Metrics -------------------------------------------------------------------------------------------------------
    static class MetricsHolder {
        // 请求处理耗时统计(从request被解码开始, 到response数据被刷到OS内核缓冲区为止)
        static final Timer processingTimer              = Metrics.timer("processing");
        // 请求被拒绝次数统计
        static final Meter rejectionMeter               = Metrics.meter("rejection");
    }
}
