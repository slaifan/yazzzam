package uk.ac.ed.yazzzam.compression;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Variable-byte integer encoder/decoder.
 * Provides an API to:
 *  - encode a list of integers into a compressed byte stream
 *  - decode a byte stream into a list of integers
 *  - decode a single integer at a specified offset of the byte stream
 * For the details of the algorithm see: https://nlp.stanford.edu/IR-book/html/htmledition/variable-byte-codes-1.html
 */
public class VariableByte {
    private static byte[] encodeNumber(int n) {
        if (n == 0) {
            return new byte[]{ -128 };
        }
        int i = (int) (Math.log(n) / Math.log(128)) + 1;
        byte[] rv = new byte[i];
        int j = i - 1;
        do {
            rv[j--] = (byte) (n % 128);
            n /= 128;
        } while (j >= 0);
        rv[i - 1] += 128;
        return rv;
    }

    public static byte[] encode(List<Integer> numbers) {
        ByteBuffer buf = ByteBuffer.allocate((numbers.size() + 1) * (Integer.SIZE / Byte.SIZE));
        for (Integer number : numbers) {
            buf.put(encodeNumber(number));
        }
        buf.flip();
        byte[] rv = new byte[buf.limit()];
        buf.get(rv);
        return rv;
    }

    public static List<Integer> decode(byte[] byteStream) {
        List<Integer> numbers = new ArrayList<Integer>();
        var offset = 0;
        while (offset < byteStream.length) {
            var res = decodeNumber(byteStream, offset);
            numbers.add(res[0]);
            offset = res[1];
        }
        return numbers;
    }

    /**
     * Decodes a sequence of bytes at the specified offset into a single integer.
     * @param byteStream - var-byte encoded byte stream
     * @param offset - offset to the first byte of the encoded integer
     * @return an 2-element array in the format [decodedInteger, startingOffsetOfNextInteger]
     */
    public static int[] decodeNumber(byte[] byteStream, int offset) {
        int n = 0;
        for (int i = offset; i < byteStream.length; i++) {
            var b = byteStream[i];
            if (b == 0 && n == 0) {
                return new int[] { 0, i + 1 };
            }
            if ((b & 0xff) < 128) {
                n = 128 * n + b;
            } else {
                int num = (128 * n + ((b - 128) & 0xff));
                return new int[] { num, i + 1 };
            }
        }
        if (n == 0) {
            throw new IllegalStateException(String.format("Offset (%d) exceed the length of the byte stream (%d).", offset, byteStream.length));
        } else {
            throw new IllegalArgumentException("Provided byte stream sequence does not terminate with a number.");
        }
    }
}
