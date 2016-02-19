/**
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.undertow;

import ninja.Context;
import ninja.utils.NinjaPropertiesImpl;

import com.google.inject.AbstractModule;
import ninja.Bootstrap;


public class NinjaUndertowBootstrap extends Bootstrap {
    
    public NinjaUndertowBootstrap(NinjaPropertiesImpl ninjaProperties) {
        super(ninjaProperties);
    }

    @Override
    protected void configure() throws Exception {
        super.configure();

        // Context for undertow requests
        addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Context.class).to(NinjaUndertowContext.class);
            }
        });
    }
}
