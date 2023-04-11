package deque;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {
    @Test
    public void intMaxArrayDeque() {
        Comparator<Integer> intComparator = new intComparator();
        MaxArrayDeque<Integer> deque = new MaxArrayDeque<>(intComparator);
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        assertEquals(99, (double) deque.max(), 0.0);
    }

    @Test
    public void strMaxArrayDeque() {
        Comparator<String> strFirstCharComparator = new strFirstCharComparator();
        Comparator<String> strLastCharComparator = new strLastCharComparator();
        MaxArrayDeque<String> deque = new MaxArrayDeque<>(strFirstCharComparator);
        deque.addFirst("My");
        deque.addLast("Name");
        deque.addFirst("is");
        deque.addLast("Hello");
        deque.addLast("Pedro");

        assertEquals("Hello", deque.max());
        assertEquals("Name", deque.max(strLastCharComparator));
    }
}

class intComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return o1 - o2;
    }
}

class strLastCharComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        o1 = o1.toLowerCase();
        o2 = o2.toLowerCase();
        if (o1.charAt(o1.length() - 1) < o2.charAt(o2.length() - 1)) {
            return 1;
        } else if (o2.charAt(o2.length() - 1) < o1.charAt(o1.length() - 1)) {
            return -1;
        } else {
            return 0;
        }
    }
}

class strFirstCharComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        o1 = o1.toLowerCase();
        o2 = o2.toLowerCase();
        if (o1.charAt(0) < o2.charAt(0)) {
            return 1;
        } else if (o2.charAt(0) < o1.charAt(0)) {
            return -1;
        } else {
            return 0;
        }
    }
}


