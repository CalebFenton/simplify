package org.cf.smalivm;

import org.junit.BeforeClass;
import org.perfidix.annotation.BeforeBenchClass;
import org.perfidix.annotation.BeforeEachRun;
import org.perfidix.annotation.Bench;
import org.perfidix.annotation.BenchClass;

import java.math.BigInteger;
import java.util.Random;
import java.util.regex.Pattern;

@BenchClass(runs = 10)
public class MatchBenchmark {

    private static String[] data;
    private static Pattern[] patterns;
    private static Random random = new Random();
    private static int[] targets;
    private static String[] testData;

    @BeforeBenchClass
    @BeforeClass
    public static void beforeClass() {
        data = new String[10000];
        patterns = new Pattern[data.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new BigInteger(130, random).toString(32);
            patterns[i] = Pattern.compile(data[i]);
        }

        targets = new int[100];
        for (int i = 0; i < targets.length; i++) {
            targets[i] = randInt(0, data.length);
        }
    }

    private static int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    @BeforeEachRun
    public void setUp() {
        testData = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            testData[i] = new String(data[i]);
        }
    }

    @Bench
    public void timeEquals() {
        int matches = 0;
        for (int targetIndex : targets) {
            String target = testData[targetIndex];
            for (String compare : testData) {
                boolean match = target.equals(compare);
                matches += match ? 1 : 0;
            }

        }

        // System.out.println("timeEquals matches: " + matches);
    }

    @Bench
    public void timeHashCompare() {
        int matches = 0;
        for (int targetIndex : targets) {
            String target = testData[targetIndex];
            for (String compare : testData) {
                boolean match = false;
                if (target.hashCode() == compare.hashCode()) {
                    match = target.equals(compare);
                }
                matches += match ? 1 : 0;
            }

        }

        // System.out.println("timeInternCompare matches: " + matches);
    }

    @Bench
    public void timeInternCompare() {
        int matches = 0;
        for (int i = 0; i < targets.length; i++) {
            testData[i] = testData[i].intern();
        }

        for (int targetIndex : targets) {
            String target = testData[targetIndex];
            for (String compare : testData) {
                boolean match = target == compare;
                matches += match ? 1 : 0;
            }

        }

        // System.out.println("timeInternCompare matches: " + matches);
    }

    @Bench
    public void timePatternMatch() {
        int matches = 0;
        for (int targetIndex : targets) {
            Pattern target = patterns[targetIndex];
            for (String compare : testData) {
                boolean match = target.matcher(compare).matches();
                matches += match ? 1 : 0;
            }
        }

        // System.out.println("timePatternMatch matches: " + matches);
    }
}
