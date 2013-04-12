/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.barcelonamedia.uima.consumer.solrConsumer.posting;

import java.io.Writer;
import java.io.IOException;
import java.util.HashMap;

/**
 * The Class XML.
 *
 * @version $Id: XML.java 629342 2008-02-20 04:06:33Z gsingers $
 */
public final class XML {

    /**
     * Disable external instantiation.
     */
    private XML() {
      //disable external instantiation
    }

    //
    // copied from some of my personal code... -YCS
    // table created from python script.
    // only have to escape quotes in attribute values, and don't really have to
    // escape '>'
    // many chars less than 0x20 are *not* valid XML, even when escaped!
    // for example, <foo>&#0;<foo> is invalid XML.
    /** The Constant CHARDATA_ESCAPES. */
    private static final String[] CHARDATA_ESCAPES = { "#0;", "#1;", "#2;",
            "#3;", "#4;", "#5;", "#6;", "#7;", "#8;", null, null, "#11;",
            "#12;", null, "#14;", "#15;", "#16;", "#17;", "#18;", "#19;",
            "#20;", "#21;", "#22;", "#23;", "#24;", "#25;", "#26;", "#27;",
            "#28;", "#29;", "#30;", "#31;", null, null, null, null, null, null,
            "&amp;", null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null,
            null, "&lt;", null, "&gt;" };

    /** The Constant ATTRIBUTE_ESCAPES. */
    private static final String[] ATTRIBUTE_ESCAPES = { "#0;", "#1;", "#2;",
            "#3;", "#4;", "#5;", "#6;", "#7;", "#8;", null, null, "#11;",
            "#12;", null, "#14;", "#15;", "#16;", "#17;", "#18;", "#19;",
            "#20;", "#21;", "#22;", "#23;", "#24;", "#25;", "#26;", "#27;",
            "#28;", "#29;", "#30;", "#31;", null, null, "&quot;", null, null,
            null, "&amp;", null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null,
            null, null, "&lt;" };

    /*
     * """
     * Simple python script used to
     * generate the escape table above. -YCS
     * """
     * #use individual char arrays or one big char array for better efficiency
     * # or byte array?
     * #other={'&':'amp', '<':'lt', '>':'gt', "'":'apos', '"':'quot'} #
     * other={'&':'amp', '<':'lt'}
     *
     * maxi=ord(max(other.keys()))+1 table=[None] * maxi #NOTE: invalid XML
     * chars are "escaped" as #nn; *not* &#nn; because #a real XML escape would
     * cause many strict XML parsers to choke. for i in range(0x20):
     * table[i]='#%d;' % i for i in '\n\r\t ': table[ord(i)]=None for k,v in
     * other.items(): table[ord(k)]='&%s;' % v
     *
     * result="" for i in range(maxi): val=table[i] if not val: val='null' else:
     * val='"%s"' % val result += val + ','
     *
     * print result **************************************
     */
    /**
     * @param str
     *            the str
     * @param out
     *            the out
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void escapeCharData(final String str,
                                      final Writer out)
            throws IOException {
        escape(str, out, CHARDATA_ESCAPES);
    }

    /**
     * Escape attribute value.
     *
     * @param str
     *            the str
     * @param out
     *            the out
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void escapeAttributeValue(final String str,
                                            final Writer out)
            throws IOException {
        escape(str, out, ATTRIBUTE_ESCAPES);
    }

    /**
     * Escape attribute value.
     *
     * @param chars
     *            the chars
     * @param start
     *            the start
     * @param length
     *            the length
     * @param out
     *            the out
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void escapeAttributeValue(final char[] chars,
                                            final int start,
                                            final int length,
                                            final Writer out)
            throws IOException {
        escape(chars, start, length, out, ATTRIBUTE_ESCAPES);
    }

    /**
     * Write xml.
     *
     * @param out
     *            the out
     * @param tag
     *            the tag
     * @param val
     *            the val
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeXML(final Writer out,
                                      final String tag,
                                      final String val)
            throws IOException {
        out.write('<');
        out.write(tag);
        if (val == null) {
            out.write('/');
            out.write('>');
        } else {
            out.write('>');
            escapeCharData(val, out);
            out.write('<');
            out.write('/');
            out.write(tag);
            out.write('>');
        }
    }

    /**
     * does NOT escape character data in val, must already be valid XML.
     *
     * @param out
     *            the out
     * @param tag
     *            the tag
     * @param val
     *            the val
     * @param attrs
     *            the attrs
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeUnescapedXML(final Writer out,
                                               final String tag,
                                               final String val,
                                               final HashMap<String, String> attrs)
            throws IOException {
        out.write('<');
        out.write(tag);
        for (String attrName : attrs.keySet()) {
            out.write(' ');
            out.write(attrName);
            out.write('=');
            out.write('"');
            out.write(attrs.get(attrName));
            out.write('"');
        }
        if (val == null) {
            out.write('/');
            out.write('>');
        } else {
            out.write('>');
            out.write(val);
            out.write('<');
            out.write('/');
            out.write(tag);
            out.write('>');
        }
    }

    /**
     * escapes character data in val.
     *
     * @param out
     *            the out
     * @param tag
     *            the tag
     * @param val
     *            the val
     * @param attrs
     *            the attrs
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void writeXML(final Writer out,
                                      final String tag,
                                      final String val,
                                      final HashMap<String, String> attrs)
            throws IOException {
        out.write('<');
        out.write(tag);
        for (String attrName : attrs.keySet()) {
            out.write(' ');
            out.write(attrName);
            out.write('=');
            out.write('"');
            escapeAttributeValue(attrs.get(attrName), out);
            out.write('"');
        }
        if (val == null) {
            out.write('/');
            out.write('>');
        } else {
            out.write('>');
            escapeCharData(val, out);
            out.write('<');
            out.write('/');
            out.write(tag);
            out.write('>');
        }
    }

    /**
     * Escape.
     *
     * @param chars
     *            the chars
     * @param offset
     *            the offset
     * @param length
     *            the length
     * @param out
     *            the out
     * @param escapes
     *            the escapes
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void escape(final char[] chars,
                               final int offset,
                               final int length,
                               final Writer out,
                               final String[] escapes)
            throws IOException {
        for (int i = offset; i < length; i++) {
            char ch = chars[i];
            if (ch < escapes.length) {
                String replacement = escapes[ch];
                if (replacement != null) {
                    out.write(replacement);
                    continue;
                }
            }
            out.write(ch);
        }
    }

    /**
     * Escape.
     *
     * @param str
     *            the str
     * @param out
     *            the out
     * @param escapes
     *            the escapes
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void escape(final String str,
                               final Writer out,
                               final String[] escapes)
            throws IOException {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch < escapes.length) {
                String replacement = escapes[ch];
                if (replacement != null) {
                    out.write(replacement);
                    continue;
                }
            }
            out.write(ch);
        }
    }
}
