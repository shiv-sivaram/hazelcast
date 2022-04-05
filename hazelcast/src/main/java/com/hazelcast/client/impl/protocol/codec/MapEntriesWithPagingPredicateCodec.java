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

package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.Generated;
import com.hazelcast.client.impl.protocol.codec.builtin.*;
import com.hazelcast.client.impl.protocol.codec.custom.*;

import javax.annotation.Nullable;

import static com.hazelcast.client.impl.protocol.ClientMessage.*;
import static com.hazelcast.client.impl.protocol.codec.builtin.FixedSizeTypesCodec.*;

/*
 * This file is auto-generated by the Hazelcast Client Protocol Code Generator.
 * To change this file, edit the templates or the protocol
 * definitions on the https://github.com/hazelcast/hazelcast-client-protocol
 * and regenerate it.
 */

/**
 * Queries the map based on the specified predicate and returns the matching entries. Specified predicate
 * runs on all members in parallel. The collection is NOT backed by the map, so changes to the map are NOT reflected
 * in the collection, and vice-versa. This method is always executed by a distributed query, so it may throw a
 * QueryResultSizeExceededException if query result size limit is configured.
 */
@Generated("077fbff7a1c7cdab9733eb5b81bc42c3")
public final class MapEntriesWithPagingPredicateCodec {
    //hex: 0x013600
    public static final int REQUEST_MESSAGE_TYPE = 79360;
    //hex: 0x013601
    public static final int RESPONSE_MESSAGE_TYPE = 79361;
    private static final int REQUEST_INITIAL_FRAME_SIZE = PARTITION_ID_FIELD_OFFSET + INT_SIZE_IN_BYTES;
    private static final int RESPONSE_INITIAL_FRAME_SIZE = RESPONSE_BACKUP_ACKS_FIELD_OFFSET + BYTE_SIZE_IN_BYTES;

    private MapEntriesWithPagingPredicateCodec() {
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class RequestParameters {

        /**
         * name of map
         */
        public java.lang.String name;

        /**
         * specified query criteria.
         */
        public com.hazelcast.client.impl.protocol.codec.holder.PagingPredicateHolder predicate;
    }

    public static ClientMessage encodeRequest(java.lang.String name, com.hazelcast.client.impl.protocol.codec.holder.PagingPredicateHolder predicate) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        clientMessage.setRetryable(true);
        clientMessage.setOperationName("Map.EntriesWithPagingPredicate");
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[REQUEST_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, REQUEST_MESSAGE_TYPE);
        encodeInt(initialFrame.content, PARTITION_ID_FIELD_OFFSET, -1);
        clientMessage.add(initialFrame);
        StringCodec.encode(clientMessage, name);
        PagingPredicateHolderCodec.encode(clientMessage, predicate);
        return clientMessage;
    }

    public static MapEntriesWithPagingPredicateCodec.RequestParameters decodeRequest(ClientMessage clientMessage) {
        ClientMessage.ForwardFrameIterator iterator = clientMessage.frameIterator();
        RequestParameters request = new RequestParameters();
        //empty initial frame
        iterator.next();
        request.name = StringCodec.decode(iterator);
        request.predicate = PagingPredicateHolderCodec.decode(iterator);
        return request;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD"})
    public static class ResponseParameters {

        /**
         * key-value pairs for the query.
         */
        public java.util.List<java.util.Map.Entry<com.hazelcast.internal.serialization.Data, com.hazelcast.internal.serialization.Data>> response;

        /**
         * The updated anchor list.
         */
        public com.hazelcast.client.impl.protocol.codec.holder.AnchorDataListHolder anchorDataList;
    }

    public static ClientMessage encodeResponse(java.util.Collection<java.util.Map.Entry<com.hazelcast.internal.serialization.Data, com.hazelcast.internal.serialization.Data>> response, com.hazelcast.client.impl.protocol.codec.holder.AnchorDataListHolder anchorDataList) {
        ClientMessage clientMessage = ClientMessage.createForEncode();
        ClientMessage.Frame initialFrame = new ClientMessage.Frame(new byte[RESPONSE_INITIAL_FRAME_SIZE], UNFRAGMENTED_MESSAGE);
        encodeInt(initialFrame.content, TYPE_FIELD_OFFSET, RESPONSE_MESSAGE_TYPE);
        clientMessage.add(initialFrame);

        EntryListCodec.encode(clientMessage, response, DataCodec::encode, DataCodec::encode);
        AnchorDataListHolderCodec.encode(clientMessage, anchorDataList);
        return clientMessage;
    }

    public static MapEntriesWithPagingPredicateCodec.ResponseParameters decodeResponse(ClientMessage clientMessage) {
        ClientMessage.ForwardFrameIterator iterator = clientMessage.frameIterator();
        ResponseParameters response = new ResponseParameters();
        //empty initial frame
        iterator.next();
        response.response = EntryListCodec.decode(iterator, DataCodec::decode, DataCodec::decode);
        response.anchorDataList = AnchorDataListHolderCodec.decode(iterator);
        return response;
    }
}
