/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.client.impl.protocol.task.list;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.ListGetCodec;
import com.hazelcast.client.impl.protocol.task.AbstractPartitionMessageTask;
import com.hazelcast.collection.impl.list.ListService;
import com.hazelcast.collection.impl.list.operations.ListGetOperation;
import com.hazelcast.instance.impl.Node;
import com.hazelcast.internal.nio.Connection;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.security.permission.ListPermission;
import com.hazelcast.spi.impl.operationservice.Operation;

import java.security.Permission;

/**
 * Client Protocol Task for handling messages with type ID:
 * {@link com.hazelcast.client.impl.protocol.codec.ListMessageType#LIST_GET}
 */
public class ListGetMessageTask
        extends AbstractPartitionMessageTask<ListGetCodec.RequestParameters> {

    public ListGetMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected Operation prepareOperation() {
        return new ListGetOperation(parameters.name, parameters.index);
    }

    @Override
    protected ListGetCodec.RequestParameters decodeClientMessage(ClientMessage clientMessage) {
        return ListGetCodec.decodeRequest(clientMessage);
    }

    @Override
    protected ClientMessage encodeResponse(Object response) {
        return ListGetCodec.encodeResponse((Data) response);
    }

    @Override
    public String getServiceName() {
        return ListService.SERVICE_NAME;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{parameters.index};
    }

    @Override
    public Permission getRequiredPermission() {
        return new ListPermission(parameters.name, ActionConstants.ACTION_READ);
    }

    @Override
    public String getMethodName() {
        return "get";
    }

    @Override
    public String getDistributedObjectName() {
        return parameters.name;
    }

}
