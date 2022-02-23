package uk.ac.ed.yazzzam.compression;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariableByteTest {
    @Test
    void DecodesCompressedPostingList() {
        var list = List.of(1);
        var encodedList = VariableByte.encode(list);
        assertEquals(1, encodedList.length);

    }
}