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
package undertow.example.controllers;

import java.util.List;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import undertow.example.models.BasicForm;

public class Application {
    
    public Result home() {
        return Results
            .ok()
            .html()
            .renderRaw("Hello World<br/><a href='/test'>Test</a>");
    }
    
    public Result test() {
        return Results
            .ok()
            .html()
            .renderRaw("This test worked");
    }
    
    public Result parameters(Context context, @Param("a") String a, @Param("b") Integer b) {
        // simple way to test context functions
        assert(a.equals(context.getParameter("a")));
        assert(context.getParameterValues("a").size() == 1);
        assert(b.equals(context.getParameterAs("b", Integer.class)));
        assert(context.getParameterValues("b").size() == 1);
        
        return Results
            .ok()
            .html()
            .renderRaw("a=" + a + ", b=" + b);
    }
    
    public Result benchmark_form(Context context, @Param("a") String a, @Param("b") Integer b, BasicForm form) {
        Cookie testCookie = context.getCookie("TEST");
        String contentType = context.getHeader("Content-Type");
        String aParam = context.getParameter("a");
        String bParam = context.getParameter("b");
        return Results
            .ok()
            .html()
            .renderRaw("benchmark");
    }
    
    public Result benchmark_json(Context context, @Param("a") String a, @Param("b") Integer b, BasicForm form) {
        Cookie testCookie = context.getCookie("TEST");
        String contentType = context.getHeader("Content-Type");
        String aParam = context.getParameter("a");
        String bParam = context.getParameter("b");
        return Results
            .ok()
            .html()
            .renderRaw("benchmark");
    }
    
    public Result basic_form(BasicForm form) {
        return Results
            .ok()
            .html()
            .renderRaw("s=" + form.getS() + ", i=" + form.getI() + ", l=" + form.getL() + ", b=" + form.getB());
    }
    
    public Result scheme(Context context) {
        return Results
            .ok()
            .html()
            .renderRaw(context.getScheme());
    }
    
    public Result request_path(Context context) {
        return Results
            .ok()
            .html()
            .renderRaw(context.getRequestPath());
    }
    
    public Result remote_addr(Context context) {
        return Results
            .ok()
            .html()
            .renderRaw(context.getRemoteAddr());
    }
    
}
