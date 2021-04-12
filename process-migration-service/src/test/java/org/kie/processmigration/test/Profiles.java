/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.processmigration.test;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Profiles {

    public static class KieServerIntegrationProfile implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(ContainerKieServerLifecycleManager.class));
        }
    }

    public static class MockKieServerProfile implements QuarkusTestProfile {
        @Override
        public List<TestResourceEntry> testResources() {
            return Collections.singletonList(new TestResourceEntry(MockKieServerLifecycleManager.class));
        }
    }
}
