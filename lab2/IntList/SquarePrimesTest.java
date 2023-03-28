package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimeNoPrimes() {
        IntList lst = IntList.of(4,6,9,12,15,18,26,30);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 6 -> 9 -> 12 -> 15 -> 18 -> 26 -> 30", lst.toString());
        assertFalse(changed);
    }

    @Test
    public void testSquarePrimeAtEnd() {
        IntList lst = IntList.of(4,6,9,12,17);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 6 -> 9 -> 12 -> 289", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimeSeveralPrimes() {
        IntList lst = IntList.of(4,6,9,13,15,17,26,29);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 6 -> 9 -> 169 -> 15 -> 289 -> 26 -> 841", lst.toString());
        assertTrue(changed);
    }
}
