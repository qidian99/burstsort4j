/*
 * Copyright 2008-2011 Nathan Fiedler. All rights reserved.
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file.
 */
package org.burstsort4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the MultikeyQuicksort implementation.
 *
 * @author Nathan Fiedler
 */
public class MultikeyQuicksortTest {

    @Test
    public void testArguments() {
        MultikeyQuicksort.sort((String[]) null);
        MultikeyQuicksort.sort(new String[0]);
        String[] arr = new String[]{"a"};
        MultikeyQuicksort.sort(arr);
        arr = new String[]{"b", "a"};
        MultikeyQuicksort.sort(arr);
        assertTrue(Tests.isSorted(arr));
        arr = new String[]{"c", "b", "a"};
        MultikeyQuicksort.sort(arr);
        assertTrue(Tests.isSorted(arr));
        // test with all empty input
        arr = new String[]{"", "", "", "", "", "", "", "", "", ""};
        MultikeyQuicksort.sort(arr);
        for (String s : arr) {
            assertEquals("", s);
        }
        // test with peculiar input
        arr = new String[]{"z", "m", "", "a", "d", "tt", "tt", "tt", "foo", "bar"};
        MultikeyQuicksort.sort(arr);
        assertTrue("peculiar input not sorted", Tests.isSorted(arr));
    }

    @Test
    public void testDictWords() {
        try {
            List<String> data = Tests.loadData();
            Collections.shuffle(data);
            String[] arr = data.toArray(new String[data.size()]);
            MultikeyQuicksort.sort(arr);
            assertTrue(Tests.isSorted(arr));
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    @Test
    public void testSorted() {
        try {
            List<String> data = Tests.loadData();
            Collections.sort(data);
            String[] arr = data.toArray(new String[data.size()]);
            MultikeyQuicksort.sort(arr);
            assertTrue(Tests.isSorted(arr));
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    @Test
    public void testReversed() {
        try {
            List<String> data = Tests.loadData();
            Collections.sort(data);
            Collections.reverse(data);
            String[] arr = data.toArray(new String[data.size()]);
            MultikeyQuicksort.sort(arr);
            assertTrue(Tests.isSorted(arr));
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    @Test
    public void testRepeated() {
        String[] arr = new String[10000];
        final String STR = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        Arrays.fill(arr, STR);
        MultikeyQuicksort.sort(arr);
        assertTrue(Tests.isRepeated(arr, STR));
    }

    @Test
    public void testRepeatedCycle() {
        String[] strs = new String[100];
        String seed = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        for (int i = 0, l = 1; i < strs.length; i++, l++) {
            strs[i] = seed.substring(0, l);
        }
        List<String> list = new ArrayList<String>();
        for (int c = 10000, i = 0; c > 0; i++, c--) {
            list.add(strs[i % strs.length]);
        }
        String[] arr = list.toArray(new String[list.size()]);
        MultikeyQuicksort.sort(arr);
        assertTrue(Tests.isSorted(arr));
    }

    @Test
    public void testRandom() {
        List<String> data = Tests.generateData(10000, 100);
        String[] arr = data.toArray(new String[data.size()]);
        MultikeyQuicksort.sort(arr);
        assertTrue(Tests.isSorted(arr));
    }

    @Test
    public void testHamlet() {
        try {
            List<String> data = Tests.loadData("hamletwords");
            Collections.shuffle(data);
            String[] arr = data.toArray(new String[data.size()]);
            MultikeyQuicksort.sort(arr);
            assertTrue(Tests.isSorted(arr));
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }

    @Test
    public void testDictCalls() {
        try {
            List<String> data = Tests.loadData("dictcalls.gz", true);
            String[] arr = data.toArray(new String[data.size()]);
            MultikeyQuicksort.sort(arr);
            assertTrue(Tests.isSorted(arr));
        } catch (IOException ioe) {
            fail(ioe.toString());
        }
    }
}
