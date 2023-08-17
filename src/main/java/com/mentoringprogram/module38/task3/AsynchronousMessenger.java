package com.mentoringprogram.module38.task3;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class AsynchronousMessenger {

    private static final AsynchronousQueue asynchronousQueue = new AsynchronousQueue(5);

    public static void main(String[] args) throws InterruptedException {
        Thread providerTread = new Thread(new AsynchronousMessenger.ProviderThread());
        providerTread.start();

        Thread.sleep(10000);

        Thread consumerThread = new Thread(new AsynchronousMessenger.ConsumerThread());
        consumerThread.start();

    }

    private static class ProviderThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                String message = getAlphaNumericString(10);
                try {
                    asynchronousQueue.put(message);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private synchronized String getAlphaNumericString(int n)
        {

            // length is bounded by 256 Character
            byte[] array = new byte[256];
            final Random random = new Random();
            random.nextBytes(array);

            String randomString
                    = new String(array, StandardCharsets.UTF_8);

            // Create a StringBuffer to store the result
            StringBuilder r = new StringBuilder();

            // Append first 20 alphanumeric characters
            // from the generated random String into the result
            for (int k = 0; k < randomString.length(); k++) {

                char ch = randomString.charAt(k);

                if (((ch >= 'a' && ch <= 'z')
                        || (ch >= 'A' && ch <= 'Z')
                        || (ch >= '0' && ch <= '9'))
                        && (n > 0)) {

                    r.append(ch);
                    n--;
                }
            }

            // return the resultant string
            return r.toString();
        }
    }

    private static class ConsumerThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    String message = asynchronousQueue.get();
                    System.out.println("Consumer message: " + message);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
