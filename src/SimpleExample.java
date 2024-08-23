
public class SimpleExample {

    private static final int NUM_THREADS = 4;
    private static final long ITERATIONS = 100_000_000L;
    private static volatile short[] memory = new short[NUM_THREADS];
    
    public static void main(String[] args) throws InterruptedException {
        
        long duration = runTest();
        System.out.println("Duration: " + duration / 1_000_000 + " ms");
    }

    public static long runTest() throws InterruptedException {

        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            
            final int memoryLocation = i;

            threads[i] = new Thread(() -> {

                for (long j = 0; j < ITERATIONS; j++) {

                    //flips a bit in the memory location. ex. 00000000 -> 00000001
                    memory[memoryLocation] = (byte) (memory[memoryLocation] ^ 0x01);
                }
            });
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
}
