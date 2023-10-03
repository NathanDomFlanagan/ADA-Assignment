import java.util.Arrays;

public class CurrencyConversion {

    public static double findBestConversionRate(double[][] adjacencyMatrix, int sourceCurrencyIndex, int targetCurrencyIndex, int[] predecessors, boolean checkArbitrage) {
        // 4
        int n = adjacencyMatrix.length;
    
        // Initialize the distances to all nodes to be infinity.
        double[] distances = new double[n];
        for (int i = 0; i < n; i++) {
            distances[i] = Double.POSITIVE_INFINITY;
        }
    
        // Set the distance to the source currency to be 0.
        distances[sourceCurrencyIndex] = 0;
    
        // Relax the edge weights until there are no more changes in distances.
        boolean relaxed = true;
        while (relaxed) {
            relaxed = false;
            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {
                    if (adjacencyMatrix[u][v] != Double.POSITIVE_INFINITY && distances[u] + adjacencyMatrix[u][v] < distances[v]) {
                        distances[v] = distances[u] + adjacencyMatrix[u][v];
                        predecessors[v] = u;
                        relaxed = true;
                    }
                }
            }
        }
    
        // Check for negative cycles in the graph.
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (adjacencyMatrix[u][v] != Double.POSITIVE_INFINITY && distances[u] + adjacencyMatrix[u][v] < distances[v]) {
                    // There is a negative cycle in the graph.
                    System.err.println("There is a negative cycle in the currency exchange graph.");
                    return Double.NaN;
                    // return -1;
                }
            }
        }

        // Arbitrage code
        boolean arbitrageDetected = false; // New flag to track arbitrage

        for (int sourceCurr = 0; sourceCurr < n; sourceCurr++) {
            for (int destCurr = 0; destCurr < n; destCurr++) {
                if (adjacencyMatrix[sourceCurr][destCurr] != Double.POSITIVE_INFINITY && distances[sourceCurr] + adjacencyMatrix[sourceCurr][destCurr] < distances[destCurr]) {
                    distances[destCurr] = distances[sourceCurr] + adjacencyMatrix[sourceCurr][destCurr];
                    predecessors[destCurr] = sourceCurr;
                    if (checkArbitrage) {
                        arbitrageDetected = true; // Arbitrage opportunity detected
                    }
                }
            }
        }

        if (checkArbitrage && arbitrageDetected) {
            return -1.0; // Arbitrage opportunity detected
        }
    
        return distances[targetCurrencyIndex];
    }    

    
    public static void main(String[] args) {
        // Create the adjacency matrix for the currency exchange graph.
        // double[][] adjacencyMatrix = {
        //     {0, 0.072, 0.41}, // NZD to AUD, USD
        //     {0.072, 0, 0.048}, // AUD to NZD, USD
        //     {0.41, 0.048, 0} // USD to NZD, AUD
        // };
        // double[][] adjacencyMatrix = {
        //     {1, 0.928694, 0.59695, 0.488910}, // NZD to AUD, USD, GBP
        //     {1.07678, 1, 0.642668, 0.526499}, // AUD to NZD, USD, GBP
        //     {1.67574, 1.5562, 1, 0.819572}, // USD to NZD, AUD, GBP
        //     {2.045651, 1.89968, 1.22008, 1} // GBP to NZD, AUD, USD
        // };

        // TODO: Make a Table 2 from the assignment requirement for testing purposes (might have to make a second adjacency matrix, or comment out old one?)
        //
        
        //TODO: Test Case 1
        double[][] adjacencyMatrix = {
            {0, 0.074277, 0.221868, 0.714001}, // NZD to AUD, USD, GBP
            {0.075067, 0, 0.444929, 0.644523}, // AUD to NZD, USD, GBP
            {0.510091, 0.435783, 0, 0.200406}, // USD to NZD, AUD
            {0.707958, 0.232719, 0.200642, 0} // GBP to NZD, AUD, USD
        };      
        // double[][] adjacencyMatrix = {
        //     {0, 0.5, 0.8, 0.5},  // Currency A to B, C, D
        //     {2, 0, 2, 1},        // Currency B to A, C, D (Modified rate to create arbitrage)
        //     {1.25, 0.5, 0, 0.5}, // Currency C to A, B, D
        //     {2, 1, 2, 0}         // Currency D to A, B, C
        // };
        
        // double[][] adjacencyMatrix = {
        //     {1, 0.651, -0.581},
        //     {-1.531, 1, 0.952},
        //     {1.711, 1.049, 1}
        // };
        
             
        // Currencies inside matrix
        String[] currencies = {"NZD", "AUD", "USD", "GBP"};
        // String[] currencies = {"NZD", "AUD", "USD"};
        // testCurrencyConversion(adjacencyMatrix, currencies);
        boolean arbitrageDetected = testCurrencyConversion(adjacencyMatrix, currencies);

        if (!arbitrageDetected) {
            System.out.println("\nNo arbitrage opportunities found.\n");
        }

        //TODO: Test Case 2
        // double[][] adjacencyMatrix2 = {
        //TODO: Test Case 3

        //TODO: Test Case 4
        
    }

    public static boolean testCurrencyConversion(double[][] adjacencyMatrix, String[] currencies) 
    {
        System.out.println("\nCurrency Conversion between " + Arrays.toString(currencies) + ":");
        boolean arbitrageDetected = false;

        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) 
        {
            for (int targetCurrencyIndex = 0; targetCurrencyIndex < currencies.length; targetCurrencyIndex++) {
                if (sourceCurrencyIndex != targetCurrencyIndex) {
                    int[] predecessors = new int[currencies.length];
                    double bestConversionRate = findBestConversionRate(adjacencyMatrix, sourceCurrencyIndex, targetCurrencyIndex, predecessors, true);
                    
                    if (Double.isNaN(bestConversionRate)) {
                    // if (bestConversionRate == -1) {
                        System.out.println("There is a negative cycle in the currency exchange graph, so there is no valid conversion rate.");
                        arbitrageDetected = true;
                    } else if (bestConversionRate > 1.0) {
                        System.out.println("\nArbitrage opportunity detected!");
                        arbitrageDetected = true;
                        System.out.println("Arbitrage conversion rate from " + currencies[sourceCurrencyIndex] + " to " 
                                            + currencies[targetCurrencyIndex] + " is: " + bestConversionRate);
                        System.out.print("The arbitrage sequence is: ");
                        int i = targetCurrencyIndex;

                        while(i != sourceCurrencyIndex) {
                            System.out.print(currencies[i] + " <- ");
                            i = predecessors[i];
                        }
                        System.out.println(currencies[sourceCurrencyIndex]);
                    } else {
                        // Format the conversion rate to display 6 decimal places
                        String formattedRate = String.format("%.6f", bestConversionRate);
                        System.out.println("\nThe best conversion rate from " + currencies[sourceCurrencyIndex] + " to " 
                                            + currencies[targetCurrencyIndex] + " is: " + formattedRate);
                    
                        System.out.print("The sequence of exchanges is: ");
                        int i = targetCurrencyIndex;

                        while(i != sourceCurrencyIndex) {
                            System.out.print(currencies[i] + " <- ");
                            i = predecessors[i];
                        }
                        System.out.println(currencies[sourceCurrencyIndex]);
                    }
                }
            }
        }
        return arbitrageDetected;
    }   
    // TODO: CODE TO MAYBE ADD BACK TO STUFF
    private void backToMain()
    {
        // TODO: ADD TO LINE 107+ 
        // double[][] adjacencyMatrix = {
        //     {0, -0.693147, -0.916291}, // Currency A to B and C
        //     {-0.693147, 0, -0.510826}, // Currency B to A and C
        //     {-0.916291, -0.587787, 0} // Currency C to A and B
        // };
        // String[] currencies = {"A", "B", "C"};
        // testCurrencyConversion(adjacencyMatrix, currencies);

        // Header of what currencies being used
        // System.out.println("\nCurrency Conversion between NZD, AUD, USD, and GBP:");
        
        // // Iterate over all pairs of currencies
        // for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
        //     for (int targetCurrencyIndex = 0; targetCurrencyIndex < currencies.length; targetCurrencyIndex++) {
        //         if (sourceCurrencyIndex != targetCurrencyIndex) {
        //             int[] predecessors = new int[currencies.length];
        //             // Find the best conversion rate.
        //             double bestConversionRate = findBestConversionRate(adjacencyMatrix, sourceCurrencyIndex, targetCurrencyIndex, predecessors);
                    
        //             // Print the best conversion rate, or an error message if there is a negative cycle.
        //             if (Double.isNaN(bestConversionRate)) {
        //                 System.err.println("There is a negative cycle in the currency exchange graph, so there is no valid conversion rate.");
        //             } else {
        //                 System.out.println("\nThe best conversion rate from " + currencies[sourceCurrencyIndex] + " to " 
        //                                     + currencies[targetCurrencyIndex] + " is: " + bestConversionRate);
                    
        //                 System.out.print("The sequence of exchanges is: ");
        //                 int i = targetCurrencyIndex;
        //                 while(i != sourceCurrencyIndex) {
        //                     System.out.print(currencies[i] + " <- ");
        //                     i = predecessors[i];
        //                 }
        //                 System.out.println(currencies[sourceCurrencyIndex]);
        //             }
        //         }
        //     }
        // }
    }

}
