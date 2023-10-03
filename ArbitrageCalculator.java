import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ArbitrageCalculator {

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

    public static void arbitrage(String[] currencies, double[][] ratesMatrix) {
        double[][] transGraph = negateLogarithmConverter(ratesMatrix);

        int source = 0;
        int n = transGraph.length;
        double[] minDist = new double[n];
        int[] pre = new int[n];

        Arrays.fill(minDist, Double.POSITIVE_INFINITY);
        Arrays.fill(pre, -1);

        minDist[source] = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int sourceCurr = 0; sourceCurr < n; sourceCurr++) {
                for (int destCurr = 0; destCurr < n; destCurr++) {
                    if (minDist[destCurr] > minDist[sourceCurr] + transGraph[sourceCurr][destCurr]) {
                        minDist[destCurr] = minDist[sourceCurr] + transGraph[sourceCurr][destCurr];
                        pre[destCurr] = sourceCurr;
                    }
                }
            }
        }

        for (int sourceCurr = 0; sourceCurr < n; sourceCurr++) {
            for (int destCurr = 0; destCurr < n; destCurr++) {
                if (minDist[destCurr] > minDist[sourceCurr] + transGraph[sourceCurr][destCurr]) {
                    List<Integer> printCycle = new ArrayList<>();
                    printCycle.add(destCurr);
                    printCycle.add(sourceCurr);

                    // double value = 1.0; // Initialize value with 1.0


                    while (!printCycle.contains(pre[sourceCurr])) {
                        printCycle.add(pre[sourceCurr]);
                        sourceCurr = pre[sourceCurr];
                        // value *= ratesMatrix[pre[sourceCurr]][sourceCurr]; // Calculate the value change
                    }

                    printCycle.add(pre[sourceCurr]);

                    System.out.println("Arbitrage Opportunity:");
                    for (int i = printCycle.size() - 1; i >= 0; i--) {
                        System.out.print(currencies[printCycle.get(i)]);
                        if (i != 0) {
                            System.out.print(" --> ");
                        }
                    }
                    System.out.println();
                    // System.out.println("Arbitrage Value: " + (value - 1.0)); // Display the value of the arbitrage opportunity
                }
            }
        }
    }

    public static void main(String[] args) {
        
        // TODO: The matrix given with code
        // String[] currencies = {"PLN", "EUR", "USD", "RUB", "INR", "MXN"};
        
        // Test data
        // String[] currencies = {"A", "B", "C", "D"};


        // My real world currency exchange data
        String[] currencies = {"NZD", "AUD", "USD", "GBP"};


        double[][] rates = {
        //     {1, 0.23, 0.25, 16.43, 18.21, 4.94},
        //     {4.34, 1, 1.11, 71.40, 79.09, 21.44},
        //     {3.93, 0.90, 1, 64.52, 71.48, 19.37},
        //     {0.061, 0.014, 0.015, 1, 1.11, 0.30},
        //     {0.055, 0.013, 0.014, 0.90, 1, 0.27},
        //     {0.20, 0.047, 0.052, 3.33, 3.69, 1}
        // };
        //     {0, 0.5, 0.8, 0.5},  // Currency A to B, C, D
        //     {2, 0, 2, 1},        // Currency B to A, C, D (Modified rate to create arbitrage)
        //     {1.25, 0.5, 0, 0.5}, // Currency C to A, B, D
        //     {2, 1, 2, 0}         // Currency D to A, B, C
        // };
        //     {0, 0.074277, 0.221868, 0.714001}, // NZD to AUD, USD, GBP
        //     {0.075067, 0, 0.444929, 0.644523}, // AUD to NZD, USD, GBP
        //     {0.510091, 0.435783, 0, 0.200406}, // USD to NZD, AUD Changed value of NZD to cause arbitrage opportunity
        //     {0.707958, 0.232719, 0.200642, 0} // GBP to NZD, AUD, USD Changed value of USD to cause arbitrage opportunity
        // };  
        
        // // Test arbitrages
        //     {0, 0.5, 0.8, 0.5},  // NZD to AUD, USD, GBP
        //     {2, 0, 2, 1},        // AUD to NZD, USD, GBP (Modified rate to create arbitrage)
        //     {1.25, 0.5, 0, 0.5}, // USD to NZD, AUD, GBP (Modified rate to create arbitrage)
        //     {2, 1, 2, 0}         // GBP to NZD, AUD, USD
        // };
        {0, 0.5, 0.8, 0.5},  // NZD to AUD, USD, GBP
    {2, 0, 2, 1},        // AUD to NZD, USD, GBP (Modified rate to create arbitrage)
    {1.5, 0.5, 0, 0.5},  // USD to NZD, AUD (Changed value of USD to cause arbitrage opportunity)
    {0.7, 0.232719, 0.200642, 0}  // GBP to NZD, AUD, USD (Changed value of GBP to cause arbitrage opportunity)
};


        arbitrage(currencies, rates);

        // This is just a place holder until I implement task 1 properly
        System.out.println("No arbitrage opportunities");
    }
}