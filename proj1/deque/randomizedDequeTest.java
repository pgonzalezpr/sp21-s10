package deque;

import edu.princeton.cs.algs4.StdRandom;
import static org.junit.Assert.*;
import org.junit.*;

public class randomizedDequeTest {
    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        int N = 10000;

        for (int i = 0; i < N; ++i) {
            int operationNumber = StdRandom.uniform(0, 6);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                linkedListDeque.addFirst(randVal);
                arrayDeque.addFirst(randVal);

            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                linkedListDeque.addLast(randVal);
                arrayDeque.addLast(randVal);

            } else if (operationNumber == 2) {
                assertEquals(linkedListDeque.size(), arrayDeque.size());

            } else if (operationNumber == 3 && linkedListDeque.size() > 0 && arrayDeque.size() > 0) {
                if (linkedListDeque.size() == arrayDeque.size()) {
                    int randIndex = StdRandom.uniform(arrayDeque.size());
                    assertEquals(linkedListDeque.get(randIndex), arrayDeque.get(randIndex));
                }

            } else if (operationNumber == 4 && linkedListDeque.size() > 0 && arrayDeque.size() > 0) {
                assertEquals(linkedListDeque.removeFirst(), arrayDeque.removeFirst());

            } else if (operationNumber == 5 && linkedListDeque.size() > 0 && arrayDeque.size() > 0) {
                assertEquals(linkedListDeque.removeLast(), arrayDeque.removeLast());
            }
        }
    }
}
