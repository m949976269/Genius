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

package mcc.genius.common.util;



import mcc.genius.common.util.internal.InternalThreadLocal;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

/**
 * 利用对象继承的内存布局规则来padding避免false sharing, 注意其中对象头会至少占用8个字节
 * ---------------------------------------
 *  For 32 bit JVM:
 *      _mark   : 4 byte constant
 *      _klass  : 4 byte pointer to class
 *  For 64 bit JVM:
 *      _mark   : 8 byte constant
 *      _klass  : 8 byte pointer to class
 *  For 64 bit JVM with compressed-oops:
 *      _mark   : 8 byte constant
 *      _klass  : 4 byte pointer to class
 * ---------------------------------------
 */
class LhsPadding {
    @SuppressWarnings("unused")
    protected long p01, p02, p03, p04, p05, p06, p07;
}

class Value extends LhsPadding {
    protected volatile long value;
}

class RhsPadding extends Value {
    @SuppressWarnings("unused")
    protected long p09, p10, p11, p12, p13, p14, p15;
}

/**
 * 序号生成器, 每个线程预先申请一个区间, 步长(step)固定, 以此种方式尽量减少CAS操作,
 * 需要注意的是, 这个序号生成器不是严格自增的, 并且也溢出也是可以接受的(接受负数).
 *
 * jupiter
 * org.jupiter.common.util
 *
 * @author jiachun.fjc
 */
public class Sequence extends RhsPadding {

    private static final int DEFAULT_STEP = 128;

    private static final AtomicLongFieldUpdater<Value> updater = AtomicLongFieldUpdater.newUpdater(Value.class, "value");

    private final InternalThreadLocal<LocalSequence> localSequence = new InternalThreadLocal<LocalSequence>() {

        @Override
        protected LocalSequence initialValue() throws Exception {
            return new LocalSequence();
        }
    };

    private final int step;

    public Sequence() {
        this(DEFAULT_STEP);
    }

    public Sequence(int step) {
        this.step = step;
    }

    public Sequence(long initialValue, int step) {
        updater.set(this, initialValue);
        this.step = step;
    }

    public long next() {
        return localSequence.get().next();
    }

    private long getNextBaseValue() {
        return updater.getAndAdd(this, step);
    }

    private final class LocalSequence {

        private long localBase = getNextBaseValue();
        private long localValue = 0;

        public long next() {
            long realVal = ++localValue + localBase;

            if (localValue == step) {
                localBase = getNextBaseValue();
                localValue = 0;
            }

            return realVal;
        }
    }
}
