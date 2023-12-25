package org.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static final int N = 10000;
    static final int truncation = 16;

    public static void main(String[] args) {
//        System.out.println("Hello World");
        firstAttack();
//        secondAttack();
    }

    public static double predictProbabilityByHellmannTheorem(BigDecimal k, BigDecimal l) {
        BigDecimal n = BigDecimal.TWO.pow(truncation);

        BigDecimal sum = new BigDecimal(0);
        for (BigDecimal i = BigDecimal.ONE; i.compareTo(k) <= 0; i = i.add(BigDecimal.ONE)) {
            for (BigDecimal j = BigDecimal.ZERO; j.compareTo(l) < 0; j = j.add(BigDecimal.ONE)) {
                sum = sum.add(BigDecimal.ONE.subtract(i.multiply(l).divide(n)).pow(j.add(BigDecimal.ONE).intValue()));
            }
        }

        sum = sum.divide(n);
        return sum.doubleValue();
    }

    public static void firstAttack() {
        final ArrayList<Integer> KExponents = new ArrayList<>(List.of(10, 12, 14));
        final ArrayList<Integer> LExponents = new ArrayList<>(List.of(5, 6, 7));

        for (int K : KExponents) {
            for (int L : LExponents) {
                int success = 0;
                RedundancyFunction function = new RedundancyFunction(128 - truncation);
                Map<String, String> redundancyTable = Tools.getRedundancyTable(K, L, truncation, function);

                long l = (long) Math.pow(2, L);
                for (int i = 0; i < N; i++) {
                    String randomHash = Tools.hash(Tools.getRandomBytes(256)).substring(36).toLowerCase();
                    for (long j = 0L; j < l; j++) {
                        if (redundancyTable.containsValue(randomHash)) {
                            success++;
                            break;
                        } else {
                            randomHash = Tools.hash(function.get(randomHash)).substring(36).toLowerCase();
                        }
                    }
                }

                BigDecimal kForPrediction = BigDecimal.TWO.pow(K);
                BigDecimal lForPrediction = BigDecimal.TWO.pow(L);

                //Result section
                System.out.println("***** New Attack *****");
                System.out.println("K: 2^" + K);
                System.out.println("L: 2^" + L);
                System.out.println("Prediction by Hellmann: " + predictProbabilityByHellmannTheorem(kForPrediction, lForPrediction));
                System.out.println("Success rate: " + (double) success/N);
                System.out.println();
            }
        }
    }

    public static void secondAttack() {
        final ArrayList<Integer> KExponents = new ArrayList<>(List.of(5, 6, 7));
        final ArrayList<Integer> LExponents = new ArrayList<>(List.of(5, 6, 7));

        for (int K : KExponents) {
            for (int L : LExponents) {
                int success = 0;
                Map<RedundancyFunction, Map<String, String>> redundancyTables = new HashMap<>();

                long k = (long) Math.pow(2, K);
                long l = (long) Math.pow(2, L);
                for (long i = 0L; i < k; i++) {
                    RedundancyFunction function = new RedundancyFunction(128 - truncation);
                    Map<String, String> table = Tools.getRedundancyTable(K, L, truncation, function);
                    redundancyTables.put(function, table);
                }

                for (int i = 0; i < N; i++) {
                    ArrayList<String> hashes = new ArrayList<>();
                    String randomHash = Tools.hash(Tools.getRandomBytes(256)).substring(36).toLowerCase();
                    for (long j = 0L; j < l; j++) {
                        hashes.add(randomHash);
                    }
                    boolean out = false;
                    for (long j = 0L; j < l; j++) {
                        for (Map.Entry<RedundancyFunction, Map<String, String>> entry : redundancyTables.entrySet()) {
                            if (entry.getValue().containsValue(hashes.get((int) j))) {
                                success++;
                                out = true;
                                break;
                            } else {
                                hashes.set((int) j, Tools.hash(entry.getKey().get(hashes.get((int) j))).substring(36).toLowerCase());
                            }
                        }
                        if (out) break;
                    }
                }

                BigDecimal kForPrediction = BigDecimal.TWO.pow(K);
                BigDecimal lForPrediction = BigDecimal.TWO.pow(L);

                System.out.println("***** New Attack *****");
                System.out.println("K: 2^" + K);
                System.out.println("L: 2^" + L);
                System.out.println("Prediction by Hellmann: " + predictProbabilityByHellmannTheorem(kForPrediction , lForPrediction));
                System.out.println("Success rate: " + (double) success/N);
                System.out.println();
            }
        }
    }
}