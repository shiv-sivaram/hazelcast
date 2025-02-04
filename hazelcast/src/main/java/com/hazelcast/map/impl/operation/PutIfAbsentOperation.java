/*
 * Copyright (c) 2008-2022, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.map.impl.operation;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.map.impl.ImmutableMapSupport;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.spi.impl.operationservice.MutatingOperation;

import static com.hazelcast.map.impl.record.Record.UNSET;

public class PutIfAbsentOperation extends BasePutOperation implements MutatingOperation {

    protected transient boolean successful;

    public PutIfAbsentOperation(String name, Data dataKey, Object value) {
        super(name, dataKey, value);
    }

    public PutIfAbsentOperation() {
    }

    @Override
    protected void runInternal() {

        if (mapContainer.getMapConfig().getInMemoryFormat() == InMemoryFormat.OBJECT) {

            if (dataValue instanceof Data) {
                // At this point, dataValue should be deserialized. So, it cannot be an instance of Data
                throw new IllegalStateException("Unexpected type for data value in putIfAbsent. " + dataValue);
            } else {
                if (ImmutableMapSupport.isConsideredImmutable(dataValue, this)) {
                    oldValue = recordStore.putIfAbsent(dataKey, dataValue, getTtl(), getMaxIdle(), getCallerAddress());
                } else {
                    oldValue = recordStore.putIfAbsent(dataKey, ImmutableMapSupport.defensiveCopy(dataValue, mapServiceContext),
                        getTtl(), getMaxIdle(), getCallerAddress());
                }
            }
        } else {
            oldValue = recordStore.putIfAbsent(dataKey, mapServiceContext.toData(dataValue), getTtl(), getMaxIdle(),
                getCallerAddress());
        }
        successful = this.oldValue == null;
    }

    protected long getTtl() {
        return UNSET;
    }

    protected long getMaxIdle() {
        return UNSET;
    }

    @Override
    protected void afterRunInternal() {
        if (successful) {
            super.afterRunInternal();
        }
    }

    @Override
    public Object getResponse() {
        return oldValue;
    }

    @Override
    public boolean shouldBackup() {
        return successful && super.shouldBackup();
    }

    @Override
    public int getClassId() {
        return MapDataSerializerHook.PUT_IF_ABSENT;
    }
}
