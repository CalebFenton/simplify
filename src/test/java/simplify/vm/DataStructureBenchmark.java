package simplify.vm;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.junit.BeforeClass;
import org.perfidix.annotation.BeforeBenchClass;
import org.perfidix.annotation.Bench;
import org.perfidix.annotation.BenchClass;

import util.SparseArray;

@BenchClass(runs = 100)
public class DataStructureBenchmark {

    private static Integer[] data = new Integer[1000 * 1000];
    private static SparseArray<Object> mySparse = new SparseArray<Object>();
    private static TIntObjectMap<Object> myTrove = new TIntObjectHashMap<Object>();

    @BeforeBenchClass
    @BeforeClass
    public static void beforeClass() {
        for (int i = 0; i < data.length; i++) {
            data[i] = new Integer(i);
            mySparse.put(i, data[i]);
            myTrove.put(i, data[i]);
        }
    }

    @Bench
    public void timeInsertAndLookupSparseArray() {
        SparseArray<Object> sparse = new SparseArray<Object>();
        Object value = null;
        for (int i = 0; i < data.length; i++) {
            sparse.put(i, data[i]);
            // int key = sparse.keyAt(i);
            value = sparse.get(i);
        }

        value.toString();
    }

    @Bench
    public void timeInsertAndLookupTrove() {
        TIntObjectMap<Object> trove = new TIntObjectHashMap<Object>();
        Object value = null;
        for (int i = 0; i < data.length; i++) {
            trove.put(i, data[i]);
            value = trove.get(i);
        }

        value.toString();
    }

    @Bench
    public void timeLookupSparseArray() {
        Object value = null;
        for (int i = 0; i < data.length; i++) {
            int key = mySparse.keyAt(i);
            value = mySparse.get(key);
        }

        value.toString();
    }

    @Bench
    public void timeLookupTrove() {
        Object value = null;
        for (int i = 0; i < data.length; i++) {
            value = myTrove.get(i);
        }

        value.toString();
    }
}
