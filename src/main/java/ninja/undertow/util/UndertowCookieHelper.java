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

package ninja.undertow.util;

import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndertowCookieHelper {
    private static final Logger log = LoggerFactory.getLogger(UndertowCookieHelper.class);

    static public Cookie convertNinjaCookieToUndertowCookie(ninja.Cookie ninjaCookie) {
        Cookie undertowCookie = new CookieImpl(ninjaCookie.getName(), ninjaCookie.getValue());
        
        undertowCookie.setMaxAge(ninjaCookie.getMaxAge());
        
        if (ninjaCookie.getComment() != null) {
            undertowCookie.setComment(ninjaCookie.getComment());
        }
        
        if (ninjaCookie.getDomain() != null) {
            undertowCookie.setDomain(ninjaCookie.getDomain());
        }
        
        if (ninjaCookie.getPath() != null) {
            undertowCookie.setPath(ninjaCookie.getPath());
        }
        
        undertowCookie.setSecure(ninjaCookie.isSecure());
        undertowCookie.setHttpOnly(ninjaCookie.isHttpOnly());
        
        // TODO: discard, version, and expires???
        
        return undertowCookie;
    }
    
    static public ninja.Cookie convertUndertowCookieToNinjaCookie(@NotNull Cookie undertowCookie) {
        ninja.Cookie.Builder ninjaCookieBuilder
            = ninja.Cookie.builder(undertowCookie.getName(), undertowCookie.getValue());

        if (undertowCookie.getMaxAge() != null) {
            ninjaCookieBuilder.setMaxAge(undertowCookie.getMaxAge());
        }
        
        if (undertowCookie.getComment() != null) {
            ninjaCookieBuilder.setComment(undertowCookie.getComment());
        }
        
        if (undertowCookie.getDomain() != null) {
            ninjaCookieBuilder.setDomain(undertowCookie.getDomain());
        }
        
        if (undertowCookie.getPath() != null) {
            ninjaCookieBuilder.setPath(undertowCookie.getPath());
        }
        
        ninjaCookieBuilder.setHttpOnly(undertowCookie.isHttpOnly());
        ninjaCookieBuilder.setSecure(undertowCookie.isSecure());
        
        // TODO: discard, version, and expires???
        
        return ninjaCookieBuilder.build();
    }

}
