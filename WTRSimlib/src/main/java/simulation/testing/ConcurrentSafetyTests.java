package simulation.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import simulation.launch.*;

/**
 * Todo
 */
public class ConcurrentSafetyTests {
    SocketManager socketManager;

    public ConcurrentSafetyTests() throws IOException {
        socketManager = new SocketManager();
    }

    String getRandomString() {
        Random rand = new Random();
        StringBuilder randomString = new StringBuilder(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefg" +
            "hijklmnopqrstuvwxyz123456789");
        for (int x = 0; x < randomString.length(); x++) {
            randomString.setCharAt(x, 
                (char) ((int) (rand.nextInt(26) + 74)));
        }
        return randomString.toString();
    }

    Runnable readOutbound = new Thread(() -> {
        for (int i = 0; i < 1000; i++) {
            String j = socketManager.outboundString;
        }
    });
    
    Runnable writeOutbound = new Thread(() -> {
        for (int i = 0; i < 1000; i++) {
            socketManager.setOutboundString(getRandomString());
        }
    });

    Runnable readInbound = new Thread(() -> {
        for (int i = 0; i < 1000; i++) {
            String j = socketManager.inboundString;
        }
    });

    Runnable writeInbound = new Thread(() -> {
        for (int i = 0; i < 1000; i++) {
            socketManager.setInboundString(getRandomString());
        }
    });

    /**
     * Test to ensure threads run concurrently. 
     * @throws InterruptedException
     */
    @Test
    public void read_and_write_concurrent() throws InterruptedException {
        List<Runnable> runnables = new ArrayList<Runnable>();
        runnables.add(writeOutbound);
        runnables.add(readOutbound);
        assertConcurrent("Read and write to outbound", runnables, 10);

        runnables = new ArrayList<Runnable>();
        runnables.add(readInbound);
        runnables.add(writeInbound);
        assertConcurrent("read_and_wrte_concurrent: ", runnables, 10);
    }

    /**
     * Test to check for reading a stale string.
     * @throws InterruptedException
     */
    @Test
    public void write_equals() throws InterruptedException { 
        for (int i = 0; i < 500; i++) { 
            String randomString = getRandomString();

            for (int j = 0; j < 10; j++) {
                new Thread(() -> {
                    String instanceString = randomString;
                    for (int k = 0; k < 100; k++) {
                    
                        socketManager.setOutboundString(instanceString);
                        assertEquals("write_outbound_equals: ", instanceString, socketManager.outboundString);
                
                    }
                }).start();
            }
        }

        for (int i = 0; i < 500; i++) { 
            String randomString = getRandomString();

            for (int j = 0; j < 10; j++) {
                new Thread(() -> {
                    String instanceString = randomString;
                    for (int k = 0; k < 100; k++) {
                    
                        socketManager.setInboundString(instanceString);
                        assertEquals("write_outbound_equals: ", instanceString, socketManager.inboundString);
                
                    }
                }).start();
            }
        }
    }
    
    /**
     * Method from junit-4 documentation
     */
    public static void assertConcurrent(final String message, final List<? extends Runnable> runnables, final int maxTimeoutSeconds) throws InterruptedException {
        final int numThreads = runnables.size();
        final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<Throwable>());
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        try {
            final CountDownLatch allExecutorThreadsReady = new CountDownLatch(numThreads);
            final CountDownLatch afterInitBlocker = new CountDownLatch(1);
            final CountDownLatch allDone = new CountDownLatch(numThreads);
            for (final Runnable submittedTestRunnable : runnables) {
                threadPool.submit(new Runnable() {
                    public void run() {
                        allExecutorThreadsReady.countDown();
                        try {
                            afterInitBlocker.await();
                            submittedTestRunnable.run();
                        } catch (final Throwable e) {
                            exceptions.add(e);
                        } finally {
                            allDone.countDown();
                        }
                    }
                });
            }
            // wait until all threads are ready
            assertTrue("Timeout initializing threads! Perform long lasting initializations before passing runnables to assertConcurrent", allExecutorThreadsReady.await(runnables.size() * 10, TimeUnit.MILLISECONDS));
            // start all test runners
            afterInitBlocker.countDown();
            assertTrue(message +" timeout! More than" + maxTimeoutSeconds + "seconds", allDone.await(maxTimeoutSeconds, TimeUnit.SECONDS));
        } finally {
            threadPool.shutdownNow();
        }
        assertTrue(message + "failed with exception(s)" + exceptions, exceptions.isEmpty());
    }
}