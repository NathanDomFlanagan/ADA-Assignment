import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArbitrageCalculatorClean {

    private static final double INF = Double.POSITIVE_INFINITY;
    private static final int NO_PREVIOUS_CURRENCY = -1;

    // Define a set to keep track of detected arbitrage sequences
    private static Set<String> arbitrageSequences = new HashSet<>();

    public static double[][] negateLogarithmConverter(double[][] graph) {
        int n = graph.length;
        double[][] result = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = -Math.log(graph[i][j]);
            }
        }

        return result;
    }

    public static void findBestRateOrArbitrage(String[] currencies, double[][] ratesMatrix, int source, int destination) {
        double[][] transGraph = negateLogarithmConverter(ratesMatrix);
        int n = transGraph.length;
        double[] minDist = new double[n];
        int[] previousCurrency = new int[n];

        Arrays.fill(minDist, INF);
        Arrays.fill(previousCurrency, NO_PREVIOUS_CURRENCY);

        minDist[source] = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int sourceCurr = 0; sourceCurr < n; sourceCurr++) {
                for (int destCurr = 0; destCurr < n; destCurr++) {
                    if (minDist[destCurr] > minDist[sourceCurr] + transGraph[sourceCurr][destCurr]) {
                        minDist[destCurr] = minDist[sourceCurr] + transGraph[sourceCurr][destCurr];
                        previousCurrency[destCurr] = sourceCurr;
                    }
                }
            }
        }

        // Check for negative cycle
        for (int sourceCurr = 0; sourceCurr < n; sourceCurr++) {
            for (int destCurr = 0; destCurr < n; destCurr++) {
                if (minDist[destCurr] > minDist[sourceCurr] + transGraph[sourceCurr][destCurr]) {
                    // // Negative cycle found, print arbitrage sequence
                    // printArbitrageSequence(currencies, previousCurrency, sourceCurr);
                    // return;

                    // Construct the arbitrage sequence as a string
                    StringBuilder arbitrageSequence = new StringBuilder();
                    int curr = sourceCurr;
                    do {
                        arbitrageSequence.append(currencies[curr]).append(" --> ");
                        curr = previousCurrency[curr];
                    } while (curr != sourceCurr);

                    // Check if this arbitrage sequence has not been detected before
                    if (!arbitrageSequences.contains(arbitrageSequence.toString())) {
                        // Negative cycle found, print arbitrage sequence
                        printArbitrageSequence(currencies, previousCurrency, sourceCurr);

                        // Add the arbitrage sequence to the set of detected sequences
                        arbitrageSequences.add(arbitrageSequence.toString());
                    }

                    return;
                }
            }
        }

        // No negative cycle found, print best conversion sequence
        printBestConversionSequence(currencies, previousCurrency, source, destination, minDist);
        System.out.println("No arbitrage opportunities.");
    }

    private static void printArbitrageSequence(String[] currencies, int[] previousCurrency, int sourceCurr) {
        List<Integer> printCycle = new ArrayList<>();
        printCycle.add(sourceCurr);

        while (!printCycle.contains(previousCurrency[sourceCurr])) {
            printCycle.add(previousCurrency[sourceCurr]);
            sourceCurr = previousCurrency[sourceCurr];
        }

        printCycle.add(previousCurrency[sourceCurr]);

        System.out.println("\nArbitrage Opportunity:");
        System.out.print("Arbitrage sequence is: ");
        for (int i = printCycle.size() - 1; i >= 0; i--) {
            System.out.print(currencies[printCycle.get(i)]);
            if (i != 0) {
                System.out.print(" --> ");
            }
        }
        System.out.println();
    }

    private static void printBestConversionSequence(String[] currencies, int[] previousCurrency, int source, int destination, double[] minDist) {
        List<Integer> path = new ArrayList<>();
        for (int curr = destination; curr != NO_PREVIOUS_CURRENCY; curr = previousCurrency[curr]) {
            path.add(curr);
        }
        Collections.reverse(path);

        double bestConversionRate = Math.exp(-minDist[destination]);
        String formattedRate = new DecimalFormat("0.######").format(bestConversionRate);

        System.out.println("\nThe best conversion rate from " + currencies[source] + " to " + currencies[destination] + " is: " + formattedRate);
        System.out.print("Best conversion sequence: ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(currencies[path.get(i)]);
            if (i != path.size() - 1) {
                System.out.print(" --> ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        
        // TODO: The matrix given with code
        // String[] currencies = {"PLN", "EUR", "USD", "RUB", "INR", "MXN"};
        
        // Test data
        // String[] currencies = {"A", "B", "C", "D"};


        // My real world currency exchange data
        String[] currencies = {"NZD", "AUD", "USD", "GBP"};
        String[] currencies3 = {"NZD", "MXN", "ILS", "CHF"};
        String[] currencies2 = {"A", "B", "C", "D"};

        double[][] rates = {
        //     {1, 0.23, 0.25, 16.43, 18.21, 4.94},
        //     {4.34, 1, 1.11, 71.40, 79.09, 21.44},
        //     {3.93, 0.90, 1, 64.52, 71.48, 19.37},
        //     {0.061, 0.014, 0.015, 1, 1.11, 0.30},
        //     {0.055, 0.013, 0.014, 0.90, 1, 0.27},
        //     {0.20, 0.047, 0.052, 3.33, 3.69, 1}
        // };
            {0, 0.074277, 0.221868, 0.714001}, // NZD to AUD, USD, GBP
            {0.075067, 0, 0.444929, 0.644523}, // AUD to NZD, USD, GBP
            {0.510091, 0.435783, 0, 0.200406}, // USD to NZD, AUD Changed value of NZD to cause arbitrage opportunity
            {0.707958, 0.232719, 0.200642, 0} // GBP to NZD, AUD, USD Changed value of USD to cause arbitrage opportunity
        };  

        double[][] rates3 = {
            {0, 10.677554, 2.2797462, 0.54266646},  // New Zealand Dollar to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
            {0.09367178, 0, 0.21356324, 0.050830071}, // Mexican Peso to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
            {0.43849232, 4.681682, 0, 0.2380004}, // Israeli Shekel to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
            {1.8424752, 19.663871, 4.2006104, 0} // Swiss Franc to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
        };
        
        // // Test arbitrages
        //     {0, 0.5, 0.8, 0.5},  // NZD to AUD, USD, GBP
        //     {2, 0, 2, 1},        // AUD to NZD, USD, GBP (Modified rate to create arbitrage)
        //     {1.25, 0.5, 0, 0.5}, // USD to NZD, AUD, GBP (Modified rate to create arbitrage)
        //     {2, 1, 2, 0}         // GBP to NZD, AUD, USD
        // };

        double[][] rates2 = {
        // This causes arbitrage opportunity
            {0, 0.5, 0.8, 0.5},  // NZD to AUD, USD, GBP
            {2, 0, 2, 1},        // AUD to NZD, USD, GBP (Modified rate to create arbitrage)
            {1.5, 0.5, 0, 0.5},  // USD to NZD, AUD (Changed value of USD to cause arbitrage opportunity)
            {0.7, 0.232719, 0.200642, 0}  // GBP to NZD, AUD, USD (Changed value of GBP to cause arbitrage opportunity)
        };

        System.out.println("\nCurrency Conversion between " + Arrays.toString(currencies) + ":");

        // Call the findBestRateOrArbitrage function for all pairs of currencies
        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
            for (int destinationCurrencyIndex = 0; destinationCurrencyIndex < currencies.length; destinationCurrencyIndex++) {
                if (sourceCurrencyIndex != destinationCurrencyIndex) {
                    findBestRateOrArbitrage(currencies, rates, sourceCurrencyIndex, destinationCurrencyIndex);
                }
            }
        }

        System.out.println("\nCurrency Conversion between " + Arrays.toString(currencies3) + ":");

        // Call the findBestRateOrArbitrage function for all pairs of currencies
        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
            for (int destinationCurrencyIndex = 0; destinationCurrencyIndex < currencies.length; destinationCurrencyIndex++) {
                if (sourceCurrencyIndex != destinationCurrencyIndex) {
                    findBestRateOrArbitrage(currencies3, rates3, sourceCurrencyIndex, destinationCurrencyIndex);
                }
            }
        }

        System.out.println("\nCurrency Conversion between " + Arrays.toString(currencies2) + ":");

        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
            for (int destinationCurrencyIndex = 0; destinationCurrencyIndex < currencies.length; destinationCurrencyIndex++) {
                if (sourceCurrencyIndex != destinationCurrencyIndex) {
                    findBestRateOrArbitrage(currencies2, rates2, sourceCurrencyIndex, destinationCurrencyIndex);
                }
            }
        }
    }
}