public class CurrencyConversion {

    public static double findBestConversionRate(double[][] adjacencyMatrix, int sourceCurrencyIndex, int targetCurrencyIndex, int[] predecessors) {
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
                }
            }
        }
    
        return distances[targetCurrencyIndex];
    }    

    
    public static void main(String[] args) {
        // Create the adjacency matrix for the currency exchange graph.
        double[][] adjacencyMatrix = {
            {0, 0.072, 0.41}, // NZD to AUD, USD
            {0.072, 0, 0.048}, // AUD to NZD, USD
            {0.41, 0.048, 0} // USD to NZD, AUD
        };

        String[] currencies = {"NZD", "AUD", "USD"};

        // Iterate over all pairs of currencies
        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
            for (int targetCurrencyIndex = 0; targetCurrencyIndex < currencies.length; targetCurrencyIndex++) {
                if (sourceCurrencyIndex != targetCurrencyIndex) {
                    int[] predecessors = new int[currencies.length];
                    // Find the best conversion rate.
                    double bestConversionRate = findBestConversionRate(adjacencyMatrix, sourceCurrencyIndex, targetCurrencyIndex, predecessors);

                    // Print the best conversion rate, or an error message if there is a negative cycle.
                    if (Double.isNaN(bestConversionRate)) {
                        System.err.println("There is a negative cycle in the currency exchange graph, so there is no valid conversion rate.");
                    } else {
                        System.out.println("\nThe best conversion rate from " + currencies[sourceCurrencyIndex] + " to " + currencies[targetCurrencyIndex] + " is: " + bestConversionRate);
                    
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
    }
}

