package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> ANR = new AListNoResizing<>();
        BuggyAList<Integer> BA = new BuggyAList<>();
        for (int i = 0; i < 3; ++i) {
            int adder = StdRandom.uniform(0, 1000);
            ANR.addLast(adder);
            BA.addLast(adder);
            assertEquals(ANR.size(), BA.size());
            for (int k = 0; k < ANR.size(); ++k) {
                assertEquals(ANR.get(k), BA.get(k));
            }
        }

        for (int i = 0; i < 3; ++i) {
            ANR.removeLast();
            BA.removeLast();
            assertEquals(ANR.size(), BA.size());
            for (int k = 0; k < ANR.size(); ++k) {
                assertEquals(ANR.get(k), BA.get(k));
            }
        }

    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> A  = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                A.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int sizeA = A.size();
                assertEquals(size, sizeA);
            } else if (operationNumber == 2) {
                assertEquals(L.size(), A.size());
                if (L.size() == 0) {
                    continue;
                }
                int el = L.getLast();
                int eA = A.getLast();
                assertEquals(el, eA);
            } else if (operationNumber == 3) {
                assertEquals(L.size(), A.size());
                if (L.size() == 0) {
                    continue;
                }
                int aa = A.removeLast();
                int ll = L.removeLast();
                assertEquals(aa, ll);
            }
        }
    }

}
