/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.util.escape;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Utility functions for dealing with {@code CharEscaper}s, and some commonly used {@code
 * CharEscaper} instances.
 *
 * @since 1.0
 */
public final class CharEscapers {

  private static final Escaper URI_ESCAPER =
      new PercentEscaper(PercentEscaper.SAFECHARS_URLENCODER, true);

  private static final Escaper URI_PATH_ESCAPER =
      new PercentEscaper(PercentEscaper.SAFEPATHCHARS_URLENCODER, false);

  private static final Escaper URI_RESERVED_ESCAPER =
      new PercentEscaper(PercentEscaper.SAFE_PLUS_RESERVED_CHARS_URLENCODER, false);

  private static final Escaper URI_USERINFO_ESCAPER =
      new PercentEscaper(PercentEscaper.SAFEUSERINFOCHARS_URLENCODER, false);

  private static final Escaper URI_QUERY_STRING_ESCAPER =
      new PercentEscaper(PercentEscaper.SAFEQUERYSTRINGCHARS_URLENCODER, false);

  /**
   * Escapes the string value so it can be safely included in URIs. For details on escaping URIs,
   * see <a href="http://tools.ietf.org/html/rfc3986#section-2.4">RFC 3986 - section 2.4</a>.
   *
   * <p>When encoding a String, the following rules apply:
   *
   * <ul>
   *   <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain
   *       the same.
   *   <li>The special characters ".", "-", "*", and "_" remain the same.
   *   <li>The space character " " is converted into a plus sign "+".
   *   <li>All other characters are converted into one or more bytes using UTF-8 encoding and each
   *       byte is then represented by the 3-character string "%XY", where "XY" is the two-digit,
   *       uppercase, hexadecimal representation of the byte value.
   * </ul>
   *
   * <p><b>Note</b>: Unlike other escapers, URI escapers produce uppercase hexadecimal sequences.
   * From <a href="http://tools.ietf.org/html/rfc3986">RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits for all
   * percent-encodings."</i>
   *
   * <p>This escaper has identical behavior to (but is potentially much faster than):
   *
   * <ul>
   *   <li>{@link java.net.URLEncoder#encode(String, String)} with the encoding name "UTF-8"
   * </ul>
   */
  public static String escapeUri(String value) {
    return URI_ESCAPER.escape(value);
  }

  /**
   * Percent-decodes a US-ASCII string into a Unicode string. UTF-8 encoding is used to determine
   * what characters are represented by any consecutive sequences of the form "%<i>XX</i>".
   *
   * <p>This replaces each occurrence of '+' with a space, ' '. So this method should not be used
   * for non application/x-www-form-urlencoded strings such as host and path.
   *
   * @param uri a percent-encoded US-ASCII string
   * @return a Unicode string
   */
  public static String decodeUri(String uri) {
    try {
      return URLDecoder.decode(uri, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // UTF-8 encoding guaranteed to be supported by JVM
      throw new RuntimeException(e);
    }
  }

  /**
   * Decodes the path component of a URI. This must be done via a method that does not try to
   * convert + into spaces(the behavior of {@link java.net.URLDecoder#decode(String, String)}). This
   * method will transform URI encoded value into their decoded symbols.
   *
   * <p>i.e: {@code decodePath("%3Co%3E")} would return {@code "<o>"}
   *
   * @param path the value to be decoded
   * @return decoded version of {@code path}
   */
  public static String decodeUriPath(String path) {
    if (path == null) {
      return null;
    }
    ByteBuffer buf = null;
    int length = path.length();
    StringBuilder sb = new StringBuilder(length);

    char c;
    for (int i = 0; i < length; i++) {
      c = path.charAt(i);
      if (c == '%') {
        if (i + 2 < length) {
          if (buf == null) {
            buf = ByteBuffer.allocate(Integer.SIZE / 8);
          } else {
            buf.clear();
          }
          try {
            int v = Integer.parseInt(path.substring(i + 1, i + 3), 16);
            if (v < 0) {
              throw new IllegalArgumentException(
                  "Illegal parsed value from escaped sequence, most be positive");
            }
            buf.put((byte) v);
            i += 2;
            sb.append(new String(buf.array(), 0, buf.position(), StandardCharsets.UTF_8));

          } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Illegal length following escape sequence, must be in the form %xy");
          }

        } else {
          throw new IllegalArgumentException("Illegal remaining length following escape sequence");
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * Escapes the string value so it can be safely included in URI path segments. For details on
   * escaping URIs, see <a href="http://tools.ietf.org/html/rfc3986#section-2.4">RFC 3986 - section
   * 2.4</a>.
   *
   * <p>When encoding a String, the following rules apply:
   *
   * <ul>
   *   <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain
   *       the same.
   *   <li>The unreserved characters ".", "-", "~", and "_" remain the same.
   *   <li>The general delimiters "@" and ":" remain the same.
   *   <li>The subdelimiters "!", "$", "&amp;", "'", "(", ")", "*", ",", ";", and "=" remain the
   *       same.
   *   <li>The space character " " is converted into %20.
   *   <li>All other characters are converted into one or more bytes using UTF-8 encoding and each
   *       byte is then represented by the 3-character string "%XY", where "XY" is the two-digit,
   *       uppercase, hexadecimal representation of the byte value.
   * </ul>
   *
   * <p><b>Note</b>: Unlike other escapers, URI escapers produce uppercase hexadecimal sequences.
   * From <a href="http://tools.ietf.org/html/rfc3986">RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits for all
   * percent-encodings."</i>
   */
  public static String escapeUriPath(String value) {
    return URI_PATH_ESCAPER.escape(value);
  }

  /**
   * Escapes a URI path but retains all reserved characters, including all general delimiters. That
   * is the same as {@link #escapeUriPath(String)} except that it keeps '?', '+', and '/' unescaped.
   */
  public static String escapeUriPathWithoutReserved(String value) {
    return URI_RESERVED_ESCAPER.escape(value);
  }

  /**
   * Escapes the string value so it can be safely included in URI user info part. For details on
   * escaping URIs, see <a href="http://tools.ietf.org/html/rfc3986#section-2.4">RFC 3986 - section
   * 2.4</a>.
   *
   * <p>When encoding a String, the following rules apply:
   *
   * <ul>
   *   <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain
   *       the same.
   *   <li>The unreserved characters ".", "-", "~", and "_" remain the same.
   *   <li>The general delimiter ":" remains the same.
   *   <li>The subdelimiters "!", "$", "&amp;", "'", "(", ")", "*", ",", ";", and "=" remain the
   *       same.
   *   <li>The space character " " is converted into %20.
   *   <li>All other characters are converted into one or more bytes using UTF-8 encoding and each
   *       byte is then represented by the 3-character string "%XY", where "XY" is the two-digit,
   *       uppercase, hexadecimal representation of the byte value.
   * </ul>
   *
   * <p><b>Note</b>: Unlike other escapers, URI escapers produce uppercase hexadecimal sequences.
   * From <a href="http://tools.ietf.org/html/rfc3986">RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits for all
   * percent-encodings."</i>
   *
   * @since 1.15
   */
  public static String escapeUriUserInfo(String value) {
    return URI_USERINFO_ESCAPER.escape(value);
  }

  /**
   * Escapes the string value so it can be safely included in URI query string segments. When the
   * query string consists of a sequence of name=value pairs separated by &amp;, the names and
   * values should be individually encoded. If you escape an entire query string in one pass with
   * this escaper, then the "=" and "&amp;" characters used as separators will also be escaped.
   *
   * <p>This escaper is also suitable for escaping fragment identifiers.
   *
   * <p>For details on escaping URIs, see <a
   * href="http://tools.ietf.org/html/rfc3986#section-2.4">RFC 3986 - section 2.4</a>.
   *
   * <p>When encoding a String, the following rules apply:
   *
   * <ul>
   *   <li>The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain
   *       the same.
   *   <li>The unreserved characters ".", "-", "~", and "_" remain the same.
   *   <li>The general delimiters "@" and ":" remain the same.
   *   <li>The path delimiters "/" and "?" remain the same.
   *   <li>The subdelimiters "!", "$", "'", "(", ")", "*", ",", and ";", remain the same.
   *   <li>The space character " " is converted into %20.
   *   <li>The equals sign "=" is converted into %3D.
   *   <li>The ampersand "&amp;" is converted into %26.
   *   <li>All other characters are converted into one or more bytes using UTF-8 encoding and each
   *       byte is then represented by the 3-character string "%XY", where "XY" is the two-digit,
   *       uppercase, hexadecimal representation of the byte value.
   * </ul>
   *
   * <p><b>Note</b>: Unlike other escapers, URI escapers produce uppercase hexadecimal sequences.
   * From <a href="http://tools.ietf.org/html/rfc3986">RFC 3986</a>:<br>
   * <i>"URI producers and normalizers should use uppercase hexadecimal digits for all
   * percent-encodings."</i>
   */
  public static String escapeUriQuery(String value) {
    return URI_QUERY_STRING_ESCAPER.escape(value);
  }

  private CharEscapers() {}
}
