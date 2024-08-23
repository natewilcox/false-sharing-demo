import java.util.function.Function;

public class FalseSharingBenchmark {

    private static final int NUM_THREADS = 4;
    private static final long ITERATIONS = 100_000_000L;

    private static volatile short[] memoryForSlowWorker = new short[NUM_THREADS];
    private static volatile short[] memoryForFastWorker = new short[NUM_THREADS*4];

    private static int NUM_OF_TESTS = 10;
    private static long[] results = new long[NUM_OF_TESTS];
    private static long duration = 0;

    public static void main(String[] args) throws InterruptedException {
        
        for(int i = 0; i < NUM_OF_TESTS; i++) {

            results[i] = runTest(fastWorkerFactory());

            duration += results[i];
        }

        long average = duration / NUM_OF_TESTS;
        System.out.println("Average runtime: " + average / 1_000_000 + " ms");
    }

    public static long runTest(Function<Integer, Thread> workerFactory) throws InterruptedException {

        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            
            threads[i] = workerFactory.apply(i);
        }

        long start = System.nanoTime();
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }

        return System.nanoTime() - start;
    }

    public static Function<Integer, Thread> slowWorkerFactory() {

        return (memoryLocation) -> {

            return new Thread(() -> {
                for (long i = 0; i < ITERATIONS; i++) {
                    memoryForSlowWorker[memoryLocation] = (byte) (memoryForSlowWorker[0] ^ 0x80);
                }
            });
        };
    }

    public static Function<Integer, Thread> fastWorkerFactory() {

        return (memoryLocation) -> {

            return new Thread(() -> {
                for (long i = 0; i < ITERATIONS; i+=4) {
                    memoryForFastWorker[memoryLocation] = (byte) (memoryForFastWorker[0] ^ 0x80);
                }
            });
        };
    }
}
