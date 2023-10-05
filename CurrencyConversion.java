import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurrencyConversion {

    //TODO: RENAME CLASS TO CURRENCYCONVERSION WHEN SUBMITTING

    // Represents positive infinity for path distances in the algorithm
    private static final double INF = Double.POSITIVE_INFINITY;

    // Represents an invalid or nonexistent previous currency index
    private static final int NO_PREVIOUS_CURRENCY = -1;

    // Define a set to keep track of detected arbitrage sequences
    private static Set<String> arbitrageSequences = new HashSet<>();

    /**
     * Converts a given graph of exchange rates to a graph of negative logarithms
     * to prepare it for shortest path calculations.
     * 
     * @param graph The original graph of exchange rates.
     * @return A new graph with negative logarithmic values.
     */
    public static double[][] negativeLogarithmConverter(double[][] graph) {
        int n = graph.length;
        double[][] result = new double[n][n];

        // Iterate through the rows and columns of the original graph
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Convert each exchange rate to its negative logarithmic value
                result[i][j] = -Math.log(graph[i][j]);
            }
        }
        // Return the result of the conversion
        return result;
    }

    /**
     * Finds the best conversion rate from a source currency to a destination currency
     * and detects arbitrage opportunities in the currency exchange system.
     * 
     * @param currencies   An array of currency codes representing the currencies in the system.
     * @param ratesMatrix  A matrix containing exchange rates between currencies.
     * @param source       The index of the source currency in the 'currencies' array.
     * @param destination  The index of the destination currency in the 'currencies' array.
     */
    public static void findBestRateOrArbitrage(String[] currencies, double[][] ratesMatrix, int source, int destination) {
        
        // Convert the exchange rate graph to a graph of negative logarithmic values
        double[][] transGraph = negativeLogarithmConverter(ratesMatrix);
        int n = transGraph.length;
        double[] minDist = new double[n];
        int[] previousCurrency = new int[n];

        // Initialize arrays to store minimum distances and previous currency information
        Arrays.fill(minDist, INF);
        Arrays.fill(previousCurrency, NO_PREVIOUS_CURRENCY);

        // The source currency has a distance of 0
        minDist[source] = 0;

        // Perform the Bellman-Ford algorithm to find the shortest path
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
                        printArbitrageSequence(currencies, previousCurrency, sourceCurr, ratesMatrix);

                        // Add the arbitrage sequence to the set of detected sequences
                        arbitrageSequences.add(arbitrageSequence.toString());
                    }
                    return;
                }
            }
        }

        // No negative cycle found, so print best conversion sequence
        printBestConversionSequence(currencies, previousCurrency, source, destination, minDist);
        System.out.println("No arbitrage opportunities.");
    }

   
    /**
     * Prints the arbitrage sequence, exchange rates, and the type of arbitrage detected.
     * 
     * @param currencies        Array of currency symbols.
     * @param previousCurrency  Array of previous currency indices.
     * @param sourceCurr        Source currency index.
     * @param ratesMatrix       2D array of exchange rates between currencies.
     */
    private static void printArbitrageSequence(String[] currencies, int[] previousCurrency, int sourceCurr, double[][] ratesMatrix) {
        
        // Initialize a list to store the currency indices in the arbitrage sequence
        List<Integer> printCycle = new ArrayList<>();
        printCycle.add(sourceCurr);
    
        // Holds exchange rate, initialized with 1.0
        double totalExchangeRate = 1.0; 

        // Traverse the sequence to construct the arbitrage cycle and calculate exchange rates
        while (!printCycle.contains(previousCurrency[sourceCurr])) {
            printCycle.add(previousCurrency[sourceCurr]);
            int prevCurr = previousCurrency[sourceCurr];
            double exchangeRate = ratesMatrix[prevCurr][sourceCurr];
            totalExchangeRate *= exchangeRate; // Calculate the exchange rate
            sourceCurr = prevCurr;
        }
    
        // Add the previous currency of the source currency to complete the cycle
        printCycle.add(previousCurrency[sourceCurr]);
    
        // Print the detected arbitrage sequence
        System.out.println("\nArbitrage opportunity detected, the rate is: " + totalExchangeRate);
        System.out.print("Arbitrage sequence is: ");
        for (int i = printCycle.size() - 1; i >= 0; i--) {
            System.out.print(currencies[printCycle.get(i)]);
            if (i != 0) {
                System.out.print(" --> ");
            }
        }
        System.out.println();
    
        // Print type of arbitrage opportunity
        if(totalExchangeRate > 1)
        {
            System.out.println("Its a circular arbitrage.");
        } 
        if (totalExchangeRate < 1)
        {
            System.out.println("Its a round-trip arbitrage.");
        }
    }

    /**
     * Prints the best conversion sequence and rate from a source currency to a destination currency.
     * 
     * @param currencies        An array of currency codes representing the currencies in the system.
     * @param previousCurrency  An array containing the previous currency index for each currency in the sequence.
     * @param source            The index of the source currency.
     * @param destination       The index of the destination currency.
     * @param minDist           An array of minimum distances representing the shortest path distances.
     */
    private static void printBestConversionSequence(String[] currencies, int[] previousCurrency, int source, int destination, double[] minDist) {
        
        // Initialize a list to store the currency indices in the conversion sequence
        List<Integer> path = new ArrayList<>();
        
        // Traverse the sequence to construct the conversion path
        for (int curr = destination; curr != NO_PREVIOUS_CURRENCY; curr = previousCurrency[curr]) {
            path.add(curr);
        }
        Collections.reverse(path);

        // Calculate the best conversion rate using the minimum distance
        double bestConversionRate = Math.exp(-minDist[destination]);
        String formattedRate = new DecimalFormat("0.######").format(bestConversionRate);

        // Print the best conversion rate and sequence
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

    /**
     * The main function of the CurrencyConversion program.
     * It performs currency conversion calculations and detects arbitrage opportunities
     * based on provided exchange rate data and test cases.
     */
    public static void main(String[] args) {
        
        // Define arrays to store currency codes and exchange rate matrices for different scenarios

        // My real world currency exchange data
        String[] currencies = {"NZD", "AUD", "USD", "GBP"};
        String[] currencies2 = {"NZD", "MXN", "ILS", "CHF"};
        String[] currencies3 = {"A", "B", "C", "D"};

        
        // Exchange rates from NZD to AUD, USD, GBP
        // Exchange rates from AUD to NZD, USD, GBP
        // Exchange rates from USD to NZD, AUD, GBP
        // Exchange rates from GBP to NZD, AUD, USD
        double[][] rates = {
        // This doesnt cause an arbitrage opportunity, so it then prints out the best currency conversions
            {0, 0.074277, 0.221868, 0.714001}, // NZD (New Zealand Dolalr) to AUD (Australian Dollar), USD (American Dollar), GBP (Great British Pound)
            {0.075067, 0, 0.444929, 0.644523}, // AUD to NZD, USD, GBP
            {0.510091, 0.435783, 0, 0.200406}, // USD to NZD, AUD, GBP
            {0.707958, 0.232719, 0.200642, 0} // GBP to NZD, AUD, USD 
        };  

        // Exchange rates for NZD, MXN, ILS, CHF
        // Exchange rates for MXN to NZD, MXN, ILS, CHF
        // Exchange rates for ILS to NZD, MXN, ILS, CHF
        // Exchange rates for CHF to NZD, MXN, ILS, CHF
        double[][] rates2 = {
        // This causes an arbitrage opportunity
            {0, 10.677554, 2.2797462, 0.54266646},  // New Zealand Dollar to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
            {0.09367178, 0, 0.21356324, 0.050830071}, // Mexican Peso to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
            {0.43849232, 4.681682, 0, 0.2380004}, // Israeli Shekel to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
            {1.8424752, 19.663871, 4.2006104, 0} // Swiss Franc to New Zealand Dollar, Mexican Peso, Israeli New Shekel, Swiss Franc
        };
        
        // Test Case
        double[][] testCase = {
        // This causes an arbitrage opportunity
            {0, 0.5, 0.8, 0.5},  // NZD to AUD, USD, GBP
            {2, 0, 2, 1},        // AUD to NZD, USD, GBP (Modified rate to create arbitrage)
            {1.5, 0.5, 0, 0.5},  // USD to NZD, AUD (Changed value of USD to cause arbitrage opportunity)
            {0.7, 0.232719, 0.200642, 0}  // GBP to NZD, AUD, USD (Changed value of GBP to cause arbitrage opportunity)
        };

        System.out.println("\nThis is my Currency Conversion/ Arbitrage Detecting code." + "\n" 
                         + "There are 3 matrix's, two have values from 4 different currencies I gathered"
                         + "\n" + "using the website \"https://www.xe.com/currencyconverter/\".");
        
        System.out.println("\nCURRENCY CONVERSION BETWEEN " + Arrays.toString(currencies) + ":");

        // Call the findBestRateOrArbitrage function for all pairs of currencies in the first scenario
        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
            for (int destinationCurrencyIndex = 0; destinationCurrencyIndex < currencies.length; destinationCurrencyIndex++) {
                if (sourceCurrencyIndex != destinationCurrencyIndex) {
                    findBestRateOrArbitrage(currencies, rates, sourceCurrencyIndex, destinationCurrencyIndex);
                    System.out.println();
                }
            }
        }

        System.out.println("\nCURRENCY CONVERSION BETWEEN " + Arrays.toString(currencies) + ":");

        // Code to call the specific conversion, source = 0 is NZD, and destination = 3 is GBP conversion
        int source = 0;
        int destination = 3; 
        findBestRateOrArbitrage(currencies, rates, source, destination);
        System.out.println();

        System.out.println("\nCURRENCY CONVERSION BETWEEN " + Arrays.toString(currencies2) + ":");

        // Call the findBestRateOrArbitrage function for all pairs of currencies in the second scenario
        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
            for (int destinationCurrencyIndex = 0; destinationCurrencyIndex < currencies.length; destinationCurrencyIndex++) {
                if (sourceCurrencyIndex != destinationCurrencyIndex) {
                    findBestRateOrArbitrage(currencies2, rates2, sourceCurrencyIndex, destinationCurrencyIndex);
                }
            }
        }

        System.out.println();
        System.out.println("\nCURRENCY CONVERSION BETWEEN " + Arrays.toString(currencies3) + ":");

        // Call the findBestRateOrArbitrage function for all pairs of currencies in the third scenario (test case)
        for (int sourceCurrencyIndex = 0; sourceCurrencyIndex < currencies.length; sourceCurrencyIndex++) {
            for (int destinationCurrencyIndex = 0; destinationCurrencyIndex < currencies.length; destinationCurrencyIndex++) {
                if (sourceCurrencyIndex != destinationCurrencyIndex) {
                    findBestRateOrArbitrage(currencies3, testCase, sourceCurrencyIndex, destinationCurrencyIndex);
                }
            }
        }
        System.out.println();
    }
}