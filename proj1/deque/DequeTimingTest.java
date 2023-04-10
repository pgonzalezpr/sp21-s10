package deque;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.List;

public class DequeTimingTest {
    private static void printTimingTable(List<Integer> Ns, List<Double> times, List<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");

        for(int i = 0; i < Ns.size(); ++i) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / (double)opCount * 1000000.0;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }

    }

    public static void main(String[] args) {
        timeADequeConstruction();
    }

    public static void timeADequeConstruction() {
        List<Integer> Ns = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        List<Integer> opCounts = new ArrayList<>();

        for(int n = 1000; n <= 1024000; n *= 2) {
            Ns.add(n);
            Deque<Integer> deque = new ArrayDeque<>();

            for(int k = 0; k < n; ++k) {
                int randVal = StdRandom.uniform(0, 2);
                if (randVal == 0) {
                    deque.addLast(k);
                } else {
                    deque.addFirst(k);
                }
            }

            Stopwatch sw = new Stopwatch();

            for (int k = 0; k < n; ++k) {
                int randVal = StdRandom.uniform(0, 2);
                if (randVal == 0) {
                    deque.removeLast();
                } else {
                    deque.removeFirst();
                }
            }

            double timeInSeconds = sw.elapsedTime();
            times.add(timeInSeconds);
            opCounts.add(n);
        }

        printTimingTable(Ns, times, opCounts);
    }

}
