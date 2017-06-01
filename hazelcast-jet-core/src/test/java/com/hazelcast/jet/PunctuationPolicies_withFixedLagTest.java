/*
 * Copyright (c) 2008-2017, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.jet;

import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.jet.PunctuationPolicies.withFixedLag;
import static org.junit.Assert.assertEquals;

@Category(QuickTest.class)
@RunWith(HazelcastParallelClassRunner.class)
public class PunctuationPolicies_withFixedLagTest {

    private static final long LAG = 10;
    private PunctuationPolicy p = withFixedLag(LAG);

    @Test
    public void when_outOfOrderEvents_then_monotonicPunct() {
        for (int i = 0; i < 10; i++) {
            assertEquals(i - LAG, p.reportEvent(i));

            // When - older events
            // Then - the same punctuation
            assertEquals(i - LAG, p.reportEvent(i - 1));
            assertEquals(i - LAG, p.reportEvent(i - 2));
        }
    }

    @Test
    public void when_eventsStop_then_puncStops() {
        // When - an event and nothing more
        p.reportEvent(LAG);

        // Then - punctuation stops
        for (int i = 0; i < 10; i++) {
            assertEquals(0, p.getCurrentPunctuation());
        }

    }

    @Test
    public void when_noEventEver_then_minValue() {
        // When
        // (nothing)

        // Then
        for (int i = 0; i < 10; i++) {
            assertEquals(Long.MIN_VALUE, p.getCurrentPunctuation());
        }
    }
}
