/*
 * Copyright (C) 2008  Nathan Fiedler
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * $Id$
 */

package org.burstsort4j;

import java.util.Random;

/**
 * A Java implementation of multikey quicksort, translated from the
 * original C implementation by J. Bentley and R. Sedgewick, from
 * their "Fast algorithms for sorting and searching strings" paper
 * published in 1997.
 *
 * @author Nathan Fiedler
 */
public class MultikeyQuicksort {
    /** Random number generator for the randomized quicksort. */
    private static final Random RAND = new Random();

    /**
     * Retrieve the character in String s at offset d. If d is greater
     * than or equal to the length of the string, return zero. This
     * simulates fixed-length strings that are zero-padded.
     *
     * @param  s  string.
     * @param  d  offset.
     * @return  character in s at d, or zero.
     */
    private static final char charAt(String s, int d) {
        return d < s.length() ? s.charAt(d) : 0;
    }

    /**
     * Method to swap to elements in an array.
     *
     * @param  a  an array of objects.
     * @param  x  the index of the first object.
     * @param  y  the index of the second object.
     */
    private static final void swap(Object[] a, int x, int y) {
        Object tmp = a[x];
        a[x] = a[y];
        a[y] = tmp;
    }

    /**
     * Swap the elements between to subarrays.
     *
     * @param  a  the array of elements.
     * @param  i  offset of first subarray.
     * @param  j  offset of second subarray.
     * @param  n  number of elements to swap.
     */
    private static void vecswap(Object[] a, int i, int j, int n) {
        while (n-- > 0) {
            swap(a, i++, j++);
        }
    }

    /**
     * The recursive portion of multikey1.
     *
     * @param  strings  the array of strings to sort.
     * @param  base     zero-based offset into array to be considered.
     * @param  length   length of subarray to consider.
     * @param  depth    the zero-based offset into the strings.
     */
    private void ssort1(String[] strings, int base, int length, int depth) {
        if (length < 2) {
            return;
        }
        int r = RAND.nextInt(length) + base;
        swap(strings, base, r);
        int v = charAt(strings[base], depth);
        boolean allzeros = v == 0;
        int le = base + 1, lt = le;
        int gt = base + length - 1, ge = gt;
        while (true) {
            for (; lt <= gt && (r = charAt(strings[lt], depth) - v) <= 0; lt++) {
                if (r == 0) {
                    swap(strings, le++, lt);
                } else {
                    allzeros = false;
                }
            }
            for (; lt <= gt && (r = charAt(strings[gt], depth) - v) >= 0; gt--) {
                if (r == 0) {
                    swap(strings, gt, ge--);
                } else {
                    allzeros = false;
                }
            }
            if (lt > gt) {
                break;
            }
            swap(strings, lt++, gt--);
        }
        r = Math.min(le - base, lt - le);
        vecswap(strings, base, lt - r, r);
        r = Math.min(ge - gt, base + length - ge - 1);
        vecswap(strings, lt, base + length - r, r);
        r = lt - le;
        ssort1(strings, base, r, depth);
        if (!allzeros) {
            // Only descend if there was at least one string that was
            // of equal or greater length than current depth.
            ssort1(strings, base + r, le + length - ge - 1, depth + 1);
        }
        r = ge - gt;
        ssort1(strings, base + length - r, r, depth);
    }

    /**
     * Sorts the array of strings using a multikey quicksort that chooses
     * a pivot point at random and does not employ a simpler sort for
     * small subarrays.
     *
     * @param  strings  array of strings to be sorted.
     */
    public void multikey1(String[] strings) {
        if (strings == null) {
            throw new IllegalArgumentException("strings must be non-null");
        }
        ssort1(strings, 0, strings.length, 0);
    }

    /**
     * Sorts the array of strings using a multikey quicksort that chooses
     * a pivot point using a "median of three" rule (or psuedo median of
     * nine for arrays over a certain threshold). For very small subarrays,
     * an insertion sort is used.
     * 
     * <p>Only characters in the strings starting from the given offset
     * <em>d</em> are considered. That is, the method will ignore all
     * characters appearing before the <em>d</em>th character.</p>
     *
     * @param  strings  array of strings to sort.
     * @param  low      low offset into the array (inclusive).
     * @param  high     high offset into the array (exclusive).
     * @param  depth    offset of first character in each string to compare.
     */
    public static void mkqsort(String[] strings, int low, int high, int depth) {
        ssort2(strings, low, high - low, depth);
    }

    /**
     * Find the median of three characters, found in the given strings
     * at character position <em>depth</em>. One of the three integer
     * values will be returned based on the comparisons.
     *
     * @param  a      array of strings.
     * @param  l      low index.
     * @param  m      middle index.
     * @param  h      high index.
     * @param  depth  character offset.
     * @return  the position of the median string.
     */
    private static int med3(String[] a, int l, int m, int h, int depth) {
        char va = charAt(a[l], depth);
        char vb = charAt(a[m], depth);
        if (va == vb) {
            return l;
        }
        char vc = charAt(a[h], depth);
        if (vc == va || vc == vb) {
            return h;
        }
        return va < vb ? (vb < vc ? m : (va < vc ? h : l))
                : (vb > vc ? m : (va < vc ? l : h));
    }

    /**
     * The recursive portion of multikey2.
     *
     * @param  strings  the array of strings to sort.
     * @param  base     zero-based offset into array to be considered.
     * @param  length   length of subarray to consider.
     * @param  depth    the zero-based offset into the strings.
     */
    private static void ssort2(String[] a, int base, int n, int depth) {
        if (n < 8) {
            Insertionsort.sort(a, base, base + n, depth);
            return;
        }
        int pl = base;
        int pm = base + n / 2;
        int pn = base + n - 1;
        int r;
        if (n > 30) {
            // On larger arrays, find a psuedo median of nine elements.
            int d = n / 8;
            pl = med3(a, base, base + d, base + 2 * d, depth);
            pm = med3(a, base + n / 2 - d, pm, base + n / 2 + d, depth);
            pn = med3(a, base + n - 1 - 2 * d, base + n - 1 - d, pn, depth);
        }
        pm = med3(a, pl, pm, pn, depth);
        swap(a, base, pm);
        int v = charAt(a[base], depth);
        boolean allzeros = v == 0;
        int le = base + 1, lt = le;
        int gt = base + n - 1, ge = gt;
        while (true) {
            for (; lt <= gt && (r = charAt(a[lt], depth) - v) <= 0; lt++) {
                if (r == 0) {
                    swap(a, le++, lt);
                } else {
                    allzeros = false;
                }
            }
            for (; lt <= gt && (r = charAt(a[gt], depth) - v) >= 0; gt--) {
                if (r == 0) {
                    swap(a, gt, ge--);
                } else {
                    allzeros = false;
                }
            }
            if (lt > gt) {
                break;
            }
            swap(a, lt++, gt--);
        }
        pn = base + n;
        r = Math.min(le - base, lt - le);
        vecswap(a, base, lt - r, r);
        r = Math.min(ge - gt, pn - ge - 1);
        vecswap(a, lt, pn - r, r);
        if ((r = lt - le) > 1) {
            ssort2(a, base, r, depth);
        }
        if (!allzeros) {
            // Only descend if there was at least one string that was
            // of equal or greater length than current depth.
            ssort2(a, base + r, le + n - ge - 1, depth + 1);
        }
        if ((r = ge - gt) > 1) {
            ssort2(a, base + n - r, r, depth);
        }
    }


    /**
     * Sorts the array of strings using a multikey quicksort that chooses
     * a pivot point using a "median of three" rule (or psuedo median of
     * nine for arrays over a certain threshold). For very small subarrays,
     * an insertion sort is used.
     *
     * @param  strings  array of strings to be sorted.
     */
    public static void multikey2(String[] strings) {
        if (strings == null) {
            throw new IllegalArgumentException("strings must be non-null");
        }
        ssort2(strings, 0, strings.length, 0);
    }
}
