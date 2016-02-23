/*
 * Copyright 2015 joelauer.
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
package ninja.benchmark.controllers;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.benchmark.models.BasicObject;
import ninja.params.Param;

public class BenchmarkController {
    
    public Result benchmark_params(Context context, @Param("a") String a, @Param("b") String b) {
        // mock fetch things useful in a real world request like a cookie, header, and other params
        Cookie testCookie = context.getCookie("TEST");
        String contentType = context.getHeader("Content-Type");
        String cParam = context.getParameter("c");
        String dParam = context.getParameter("d");
        return Results
            .ok()
            .html()
            .renderRaw("benchmark");
    }
    
    public Result benchmark_object(Context context, BasicObject form) {
        // mock fetch things useful in a real world request like a cookie, header, and other params
        Cookie testCookie = context.getCookie("TEST");
        String contentType = context.getHeader("Content-Type");
        String aParam = context.getParameter("a");
        String bParam = context.getParameter("b");
        return Results
            .ok()
            .html()
            .renderRaw("benchmark");
    }
    
}
