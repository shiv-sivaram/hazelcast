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

import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.internal.locksupport.LockWaitNotifyKey;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.map.impl.ImmutableMapSupport;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.spi.impl.operationservice.BlockingOperation;
import com.hazelcast.spi.impl.operationservice.WaitNotifyKey;

public final class GetOperation extends ReadonlyKeyBasedMapOperation implements BlockingOperation {

    private Object result;

    public GetOperation() {
    }

    public GetOperation(String name, Data dataKey) {
        super(name, dataKey);

        this.dataKey = dataKey;
    }

    @Override
    protected void runInternal() {
        Object currentValue = recordStore.get(dataKey, false, getCallerAddress());
        if (ImmutableMapSupport.isConsideredImmutable(currentValue, this)) {
            result = currentValue;
        } else {
            result = ImmutableMapSupport.defensiveCopy(currentValue, mapServiceContext);
        }
    }

    @Override
    protected void afterRunInternal() {
        mapServiceContext.interceptAfterGet(mapContainer.getInterceptorRegistry(), result);
        super.afterRunInternal();
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(getServiceNamespace(), dataKey);
    }

    @Override
    public boolean shouldWait() {
        if (recordStore.isTransactionallyLocked(dataKey)) {
            return !recordStore.canAcquireLock(dataKey, getCallerUuid(), getThreadId());
        }
        return false;
    }

    @Override
    public void onWaitExpire() {
        sendResponse(new OperationTimeoutException("Cannot read transactionally locked entry!"));
    }

    @Override
    public Object getResponse() {
        return result;
    }

    @Override
    public int getClassId() {
        return MapDataSerializerHook.GET;
    }
}
