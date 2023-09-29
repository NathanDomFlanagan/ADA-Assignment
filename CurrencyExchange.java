// import java.util.*;

// class Edge {
//     int src, dest;
//     double weight;
//     Edge(int src, int dest, double weight) {
//         this.src = src;
//         this.dest = dest;
//         this.weight = weight;
//     }
// }

// class CurrencyExchange {
//     int V; // Number of vertices in your graph
//     ArrayList<Edge> edges = new ArrayList<Edge>(); // Your list of edges

//     void bellmanFord(int src) {
//         double dist[] = new double[V];
//         int pred[] = new int[V];

//         for (int i = 0; i < V; i++) {
//             dist[i] = Double.MAX_VALUE;
//             pred[i] = -1;
//         }
//         dist[src] = 0;

//         for (int i = 1; i < V; i++) {
//             for (Edge edge : edges) {
//                 int u = edge.src;
//                 int v = edge.dest;
//                 double weight = edge.weight;
//                 if (dist[u] != Double.MAX_VALUE && dist[u] + weight < dist[v]) {
//                     dist[v] = dist[u] + weight;
//                     pred[v] = u;
//                 }
//             }
//         }

//         for (Edge edge : edges) {
//             int u = edge.src;
//             int v = edge.dest;
//             double weight = edge.weight;
//             if (dist[u] != Double.MAX_VALUE && dist[u] + weight < dist[v]) {
//                 System.out.println("Graph contains negative weight cycle");
//                 printNegativeCycle(pred, u);
//                 return;
//             }
//         }

//         printSolution(dist, V, pred);
//     }

//     void printSolution(double dist[], int V, int pred[]) {
//         System.out.println("Vertex Distance from Source");
//         for (int i = 0; i < V; ++i)
//             System.out.println(i + "\t\t" + dist[i]);
//     }

//     void printNegativeCycle(int pred[], int src) {
//         int x = src;
//         for (int i = 0; i < V; i++)
//             x = pred[x];

//         ArrayList<Integer> path = new ArrayList<Integer>();
//         int v = x;
//         do {
//             path.add(v);
//             v = pred[v];
//         } while (v != x);

//         Collections.reverse(path);

//         System.out.println("Negative Cycle: ");
//         for (int i : path)
//             System.out.print(i + " ");
//         System.out.println();
//     }

//     public static void main(String[] args) {
//         CurrencyExchange ce = new CurrencyExchange();
        
//         // Initialize your graph here...
//         ce.V = 3; // number of currencies
//         ce.edges.add(new Edge(0, 1, -Math.log(0.93))); // NZD to AUD
//         ce.edges.add(new Edge(1, 2, -Math.log(0.64))); // AUD to USD
//         ce.edges.add(new Edge(2, 0, -Math.log(1.68))); // USD to NZD
        
//         // Call your methods here...
//         ce.bellmanFord(0); // start from NZD
//     }
// }

import java.util.*;

class Edge {
    int src, dest;
    double weight;

    Edge(int src, int dest, double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}

class CurrencyExchange {
    int V; // Number of vertices in your graph
    ArrayList<Edge> edges = new ArrayList<Edge>(); // Your list of edges
    double[] dist; // Declare dist array at the class level
    boolean containsNegativeCycle = false; // Track if a negative cycle exists

    double[] bellmanFord(int src) {
        dist = new double[V];
        int pred[] = new int[V];

        for (int i = 0; i < V; i++) {
            dist[i] = Double.MAX_VALUE;
            pred[i] = -1;
        }
        dist[src] = 0;

        for (int i = 1; i < V; i++) {
            for (Edge edge : edges) {
                int u = edge.src;
                int v = edge.dest;
                double weight = edge.weight;
                if (dist[u] != Double.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pred[v] = u;
                }
            }
        }

        for (Edge edge : edges) {
            int u = edge.src;
            int v = edge.dest;
            double weight = edge.weight;
            if (dist[u] != Double.MAX_VALUE && dist[u] + weight < dist[v]) {
                System.out.println("Graph contains negative weight cycle");
                containsNegativeCycle = true;
                printNegativeCycle(pred, u);
                return dist; // Return dist array as is (unmodified)
            }
        }

        // No negative cycle found, return dist array
        return dist;
    }

    void printSolution(double dist[], int V, int pred[]) {
        System.out.println("Vertex Distance from Source");
        for (int i = 0; i < V; ++i)
            System.out.println(i + "\t\t" + dist[i]);
    }

    void printNegativeCycle(int pred[], int src) {
        int x = src;
        for (int i = 0; i < V; i++)
            x = pred[x];

        ArrayList<Integer> path = new ArrayList<Integer>();
        int v = x;
        do {
            path.add(v);
            v = pred[v];
        } while (v != x);

        Collections.reverse(path);

        System.out.println("Negative Cycle: ");
        for (int i : path)
            System.out.print(i + " ");
        System.out.println();
    }

    // Task 1: Finding the best conversion rate from one currency to another
    double findBestConversionRate(int srcCurrency, int destCurrency) {
        double[] result = bellmanFord(srcCurrency);
        
        if (containsNegativeCycle) {
            System.out.println("Cannot calculate conversion rate due to negative cycle.");
            return -1; // Special value to indicate failure
        }
        
        return result[destCurrency];
    }

    // Task 2: Detecting arbitrage opportunities in a currency exchange system
    boolean hasArbitrage() {
        bellmanFord(0); // Start from any currency (0 in this case)
        return containsNegativeCycle;
    }

    public static void main(String[] args) {
        CurrencyExchange ce = new CurrencyExchange();

        // Initialize your graph here...
        ce.V = 3; // number of currencies
        ce.edges.add(new Edge(0, 1, -Math.log(0.93))); // NZD to AUD
        ce.edges.add(new Edge(1, 2, -Math.log(0.64))); // AUD to USD
        ce.edges.add(new Edge(2, 0, -Math.log(1.68))); // USD to NZD

        // Task 1: Find the best conversion rate
        double bestRate = ce.findBestConversionRate(0, 2); // Convert NZD to USD
        if (bestRate != -1) {
            System.out.println("Best Conversion Rate: " + Math.exp(-bestRate));
        }

        // Task 2: Detect arbitrage opportunities
        boolean arbitrageExists = ce.hasArbitrage();
        System.out.println("Arbitrage Exists: " + arbitrageExists);
    }
}

