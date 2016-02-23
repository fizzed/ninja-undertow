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
package ninja.undertow.util;

import io.undertow.server.handlers.form.FormData;
import java.util.Deque;
import java.util.Map;

public class UndertowHelper {
    
    static public void createOrMerge(Map<String, String[]> parameters, String name, Deque<FormData.FormValue> formValues) {
        String[] current = parameters.get(name);
        int index = 0;
        int size = formValues.size();

        // prepare for merge or allocate new
        if (current != null) {
            index = current.length;
            size += current.length;
            String[] future = new String[size];
            System.arraycopy(current, 0, future, 0, current.length);
            current = future;
        } else {
            current = new String[size];
        }

        // copy values!
        for (FormData.FormValue formValue : formValues) {
            current[index] = formValue.getValue();
            index++;
        }
        
        parameters.put(name, current);
    }
    
}
