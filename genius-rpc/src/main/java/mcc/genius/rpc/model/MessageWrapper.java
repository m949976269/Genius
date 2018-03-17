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

package mcc.genius.rpc.model;

import mcc.genius.common.util.Maps;
import mcc.genius.rpc.tracing.TraceId;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * Request data wrapper.
 *
 * 请求消息包装.
 *
 * jupiter
 * org.jupiter.rpc.model.metadata
 *
 * @author jiachun.fjc
 */
public class MessageWrapper implements Serializable {

    private static final long serialVersionUID = 1009813828866652852L;


    private TraceId traceId;                // 链路追踪ID(全局唯一)
    private Map<String, String> attachments;


    public TraceId getTraceId() {
        return traceId;
    }

    public void setTraceId(TraceId traceId) {
        this.traceId = traceId;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void putAttachment(String key, String value) {
        if (attachments == null) {
            attachments = Maps.newHashMap();
        }
        attachments.put(key, value);
    }


    @Override
    public String toString() {
        return "MessageWrapper{" +
                ", traceId=" + traceId +
                ", attachments=" + attachments +
                '}';
    }
}
