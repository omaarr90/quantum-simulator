package com.omaarr90.qsim.statevector.parallel;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for AmplitudeSlice record.
 *
 * <p>Tests verify correct construction, validation, and utility methods for amplitude slice
 * boundaries used in parallel quantum gate execution.
 */
class AmplitudeSliceTest {

    @Test
    void testValidConstruction() {
        AmplitudeSlice slice = new AmplitudeSlice(0, 10);
        assertEquals(0, slice.start());
        assertEquals(10, slice.end());
        assertEquals(10, slice.length());
    }

    @Test
    void testSingleElementSlice() {
        AmplitudeSlice slice = new AmplitudeSlice(5, 6);
        assertEquals(5, slice.start());
        assertEquals(6, slice.end());
        assertEquals(1, slice.length());
    }

    @Test
    void testLargeSlice() {
        AmplitudeSlice slice = new AmplitudeSlice(1000, 2048);
        assertEquals(1000, slice.start());
        assertEquals(2048, slice.end());
        assertEquals(1048, slice.length());
    }

    @Test
    void testInvalidConstruction_NegativeStart() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AmplitudeSlice(-1, 10));
        assertTrue(exception.getMessage().contains("Start index cannot be negative"));
    }

    @Test
    void testInvalidConstruction_NegativeEnd() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AmplitudeSlice(0, -1));
        assertTrue(exception.getMessage().contains("End index cannot be negative"));
    }

    @Test
    void testInvalidConstruction_StartEqualsEnd() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AmplitudeSlice(5, 5));
        assertTrue(exception.getMessage().contains("Start index must be less than end index"));
    }

    @Test
    void testInvalidConstruction_StartGreaterThanEnd() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AmplitudeSlice(10, 5));
        assertTrue(exception.getMessage().contains("Start index must be less than end index"));
    }

    @Test
    void testContains() {
        AmplitudeSlice slice = new AmplitudeSlice(10, 20);
        
        // Test boundaries
        assertFalse(slice.contains(9));   // Before start
        assertTrue(slice.contains(10));   // At start (inclusive)
        assertTrue(slice.contains(15));   // In middle
        assertTrue(slice.contains(19));   // Before end
        assertFalse(slice.contains(20));  // At end (exclusive)
        assertFalse(slice.contains(21));  // After end
    }

    @Test
    void testContains_SingleElement() {
        AmplitudeSlice slice = new AmplitudeSlice(5, 6);
        
        assertFalse(slice.contains(4));
        assertTrue(slice.contains(5));
        assertFalse(slice.contains(6));
    }

    @Test
    void testContains_EdgeCases() {
        AmplitudeSlice slice = new AmplitudeSlice(0, 1);
        
        assertTrue(slice.contains(0));
        assertFalse(slice.contains(1));
        assertFalse(slice.contains(-1));
    }

    @Test
    void testToString() {
        AmplitudeSlice slice = new AmplitudeSlice(10, 25);
        String str = slice.toString();
        
        assertTrue(str.contains("[10, 25)"));
        assertTrue(str.contains("length=15"));
    }

    @Test
    void testToString_SingleElement() {
        AmplitudeSlice slice = new AmplitudeSlice(0, 1);
        String str = slice.toString();
        
        assertTrue(str.contains("[0, 1)"));
        assertTrue(str.contains("length=1"));
    }

    @Test
    void testEquality() {
        AmplitudeSlice slice1 = new AmplitudeSlice(10, 20);
        AmplitudeSlice slice2 = new AmplitudeSlice(10, 20);
        AmplitudeSlice slice3 = new AmplitudeSlice(10, 21);
        AmplitudeSlice slice4 = new AmplitudeSlice(11, 20);
        
        assertEquals(slice1, slice2);
        assertEquals(slice1.hashCode(), slice2.hashCode());
        
        assertNotEquals(slice1, slice3);
        assertNotEquals(slice1, slice4);
    }

    @Test
    void testImmutability() {
        AmplitudeSlice slice = new AmplitudeSlice(5, 15);
        
        // Verify that the record is immutable by checking that
        // the values cannot be changed after construction
        assertEquals(5, slice.start());
        assertEquals(15, slice.end());
        assertEquals(10, slice.length());
        
        // These values should remain constant
        assertEquals(5, slice.start());
        assertEquals(15, slice.end());
        assertEquals(10, slice.length());
    }
}