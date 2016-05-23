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
package undertow.example.conf;

import undertow.example.controllers.Application;
import ninja.Router;
import ninja.application.ApplicationRoutes;

/**
 *
 * @author joelauer
 */
public class Routes implements ApplicationRoutes {
    
    @Override
    public void init(Router router) {
        router.GET().route("/").with(Application.class, "home");
        router.GET().route("/test").with(Application.class, "test");
        router.GET().route("/parameters").with(Application.class, "parameters");
        router.POST().route("/parameters").with(Application.class, "parameters");
        router.POST().route("/upload1").with(Application.class, "upload1");
        router.POST().route("/upload2").with(Application.class, "upload2");
        router.POST().route("/benchmark_form").with(Application.class, "benchmark_form");
        router.POST().route("/benchmark_json").with(Application.class, "benchmark_json");
        router.POST().route("/basic_form").with(Application.class, "basic_form");
        router.GET().route("/scheme").with(Application.class, "scheme");
        router.GET().route("/remote_addr").with(Application.class, "remote_addr");
        router.GET().route("/request_path").with(Application.class, "request_path");
    }
    
}
