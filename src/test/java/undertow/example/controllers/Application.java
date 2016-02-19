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

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import undertow.example.models.BasicForm;

/**
 *
 * @author joelauer
 */
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
