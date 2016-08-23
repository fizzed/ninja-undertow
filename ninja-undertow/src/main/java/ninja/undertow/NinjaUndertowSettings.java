/*
 * Copyright 2016 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.undertow;

import ninja.utils.OverlayedNinjaProperties;

public class NinjaUndertowSettings {

    static final public String TRACING = "undertow.tracing";
    static final public String HTTP2 = "undertow.http2";
    
    private Boolean tracing;
    private Boolean http2;
    
    public NinjaUndertowSettings() {
        this.tracing = Boolean.FALSE;
        this.http2 = Boolean.FALSE;
    }
    
    public void apply(OverlayedNinjaProperties overlayedNinjaProperties) {
        this.tracing = overlayedNinjaProperties.getBoolean(
            TRACING, this.tracing, Boolean.FALSE);
        
        this.http2 = overlayedNinjaProperties.getBoolean(
            HTTP2, this.http2, Boolean.FALSE);
    }
    
    public Boolean getTracing() {
        return tracing;
    }

    public void setTracing(Boolean tracing) {
        this.tracing = tracing;
    }

    public Boolean getHttp2() {
        return http2;
    }

    public void setHttp2(Boolean http2) {
        this.http2 = http2;
    }
    
}
