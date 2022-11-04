package com.mammb.code.jsonstruct.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 2, time = 3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MicroBenchArray {

    // ------------------------------------------------------

    //@Benchmark
    public char[] arrayAlloc32() { // 7.182 ns/op
        char[] dest = new char[32];
        return dest;
    }
    //@Benchmark
    public char[] arrayAlloc128() { //24.711 ns/op
        char[] dest = new char[128];
        return dest;
    }
    //@Benchmark
    public char[] arrayAlloc512() { // 99.898 ns/op
        char[] dest = new char[512];
        return dest;
    }
    //@Benchmark
    public char[] arrayAlloc1024() { // 232.761 ns/op
        char[] dest = new char[1024];
        return dest;
    }

    // ------------------------------------------------------

    private static final char[] src1 = new char[32];
    private static final char[] src2 = new char[128];
    private static final char[] src3 = new char[512];
    private static final char[] src4 = new char[1024];
    private static final char[] dest = new char[32];

    //@Benchmark
    public char[] arrayCopyOf1() { // 4.113 ns/op
        System.arraycopy(src1, 0, dest, 0, 16);
        return dest;
    }
    //@Benchmark
    public char[] arrayCopyOf2() { // 4.118 ns/op
        System.arraycopy(src2, 0, dest, 0, 16);
        return dest;
    }
    //@Benchmark
    public char[] arrayCopyOf3() { // 4.101 ns/op
        System.arraycopy(src3, 0, dest, 0, 16);
        return dest;
    }
    //@Benchmark
    public char[] arrayCopyOf4() { // 4.107 ns/op
        System.arraycopy(src4, 0, dest, 0, 16);
        return dest;
    }

}
