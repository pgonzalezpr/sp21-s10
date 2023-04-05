package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> noResizingAList = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();

        noResizingAList.addLast(4);
        noResizingAList.addLast(5);
        noResizingAList.addLast(6);

        buggyAList.addLast(4);
        buggyAList.addLast(5);
        buggyAList.addLast(6);

        assertEquals(noResizingAList.removeLast(), buggyAList.removeLast());
        assertEquals(noResizingAList.removeLast(), buggyAList.removeLast());
        assertEquals(noResizingAList.removeLast(), buggyAList.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), B.size());
            } else if (operationNumber == 2 && L.size() > 0 && B.size() > 0) {
                // getLast
                assertEquals(L.getLast(), B.getLast());
            } else if (operationNumber == 3 && L.size() > 0 && B.size() > 0) {
                // removeLast
                assertEquals(L.removeLast(), B.removeLast());
            }
        }
    }
}
