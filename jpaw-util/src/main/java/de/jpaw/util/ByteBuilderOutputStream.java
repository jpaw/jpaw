 /*
  * Copyright 2012 Michael Bischoff
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package de.jpaw.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/** Provides an OutputStream which writes into a ByteBuilder.
 * Java unfortunately has designed OutputStream as abstract class rather than an interface.
 * Furthermore, the OutputStream methods throw IOExceptions, which cannot happen when writing
 * into memory.
 *
 * @author mbi
 *
 */
public class ByteBuilderOutputStream extends OutputStream {
    private static final Charset DEFAULT_CHARSET = ByteArray.CHARSET_UTF8;
    private final ByteBuilder buff;

    public ByteBuilderOutputStream(final int initialSize) {
        buff = new ByteBuilder(initialSize, DEFAULT_CHARSET);
    }

    public ByteBuilderOutputStream(final ByteBuilder buff) {
        this.buff = buff;
    }

    @Override
    public void write(final int b) throws IOException {
        buff.append((byte)b);
    }

    @Override
    public void write(final byte[] b, final int offset, final int len) throws IOException {
        buff.write(b, offset, len);
    }

    public final ByteArray asByteArray() {
        return new ByteArray(buff.getCurrentBuffer(), 0, buff.length());
    }
}
