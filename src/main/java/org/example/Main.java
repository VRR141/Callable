package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        long startTs = System.currentTimeMillis(); // start time

        List<Callable<Integer>> callableList = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(texts.length);//texts.length);


        for (String text : texts) {
            Callable<Integer> logic = () -> {

                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            callableList.add(logic);
        }

        List<Future<Integer>> futureList = executorService.invokeAll(callableList);
        for (Future<Integer> future : futureList) {
            result.add(future.get());
        }

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("max: " + result.stream().max(Comparator.naturalOrder()).get());

        System.out.println("Time: " + (endTs - startTs) + "ms");

        executorService.shutdown();
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
