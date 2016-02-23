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

import java.io.IOException;
import java.util.List;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;

/**
 *
 * @author joelauer
 */
public class UndertowFileItemIterator implements FileItemIterator {
    
    final private List<UndertowFileItemStream> items;
    private int index;

    public UndertowFileItemIterator(List<UndertowFileItemStream> items) {
        this.items = items;
        this.index = 0;
    }

    @Override
    public boolean hasNext() throws FileUploadException, IOException {
        return index < items.size();
    }

    @Override
    public FileItemStream next() throws FileUploadException, IOException {
        if (index >= items.size()) {
            return null;
        }
        
        FileItemStream fis = items.get(index);
        
        index++;
        
        return fis;
    }
}
