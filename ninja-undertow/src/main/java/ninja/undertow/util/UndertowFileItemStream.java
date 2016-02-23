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

import io.undertow.server.handlers.form.FormData.FormValue;
import io.undertow.util.Headers;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

/**
 * Wraps an Undertow FormValue to supply an Apache Commons Upload FileItemStream.
 * 
 * @author joelauer
 */
public class UndertowFileItemStream implements FileItemStream {
    
    final private String name;
    final private FormValue value;

    public UndertowFileItemStream(String name, FormValue value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public InputStream openStream() throws IOException {
        return Files.newInputStream(value.getPath());
    }

    @Override
    public String getContentType() {
        return value.getHeaders().getFirst(Headers.CONTENT_TYPE);
    }

    @Override
    public String getFieldName() {
        return name;
    }
    
    @Override
    public String getName() {
        return value.getFileName();
    }

    @Override
    public boolean isFormField() {
        return value.getPath() == null;
    }

    @Override
    public FileItemHeaders getHeaders() {
        throw new UnsupportedOperationException("NinjaUndertow does not support this yet");
    }

    @Override
    public void setHeaders(FileItemHeaders fih) {
        throw new UnsupportedOperationException("NinjaUndertow does not support this yet");
    }
    
}
