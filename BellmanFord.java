// Reference code for Bellman Ford Algorithm in Java

public class BellmanFord {

    // Representation of the infinity distance
    private static final int INF = Integer.MAX_VALUE;

    public int[] bellmanFord(int[][] graph, int s) 
    {
        int N = graph.length;

        // Initialize distances, Base Case
        int[] d = new int[N];
        for (int i = 0; i < N; i++) {
            d[i] = INF;
        }
        d[s] = 0;

        // Recurrence 
        int[] d_prime = new int[N];
        
        for (int i = 1; i < N; i++) // Loop N-1 times for the case of no negative cycles
        { 
            System.arraycopy(d, 0, d_prime, 0, N);
           
            for (int u = 0; u < N; u++) 
            {
                for (int v = 0; v < N; v++)
                {
                    // Ensure we have an edge from v to u and ensure we aren't overflowing integer values
                    if (graph[v][u] != INF && d[v] != INF && d[v] + graph[v][u] < d_prime[u])
                    {
                        d_prime[u] = d[v] + graph[v][u]; 
                    }
                }
            }    
            System.arraycopy(d_prime, 0, d, 0, N);
        }

        // Return the distance array
        return d;
    }

    public static void main(String[] args) {
        int N = 5;

        int[][] graph = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                graph[i][j] = INF;
            }
        }

// Adding edges Example lec 4 on slide 21 without negative cycle. Note that node 0 here equals to node 1 in the slides. 
        // graph[0][1] = 6;
        // graph[0][3] = 7;
        // graph[1][2] = 5;
        // graph[1][3] = 8;
        // graph[1][4] = -4;
        // graph[2][1] = -2;
        // graph[3][2] = -3;
        // graph[3][4] = 9;
        // graph[4][0] = 2;
        // graph[4][2] = 7;

// Adding edges Example lec 4 with negative cycle
       graph[0][1] = 6;
       graph[0][3] = 7;
       graph[1][2] = 5;
       graph[1][3] = 8;
       graph[1][4] = -6; // This is the only difference to the above graph
       graph[2][1] = -2;
       graph[3][2] = -3;
       graph[3][4] = 9;
       graph[4][0] = 2;
       graph[4][2] = 7;


// Adding edges Example lec 4 on Slide 11 S=0, A=1, B=2, ..., G=7
//            graph[0][1] = 10;
//            graph[0][7] = 8;
//            graph[1][5] = 2;
//            graph[2][1] = 1;
//            graph[2][3] = 1;
//            graph[3][4] = 3;
//            graph[4][5] = -1;
//            graph[5][2] = -2;
//            graph[6][1] = -4;
//            graph[6][5] = -1;
//            graph[7][6] = 1;
//        
        BellmanFord bf = new BellmanFord();
        int[] distances = bf.bellmanFord(graph, 0);

        // Print distances
        for (int i = 0; i < N; i++) {
            System.out.println("Distance to node " + i + " is: " + distances[i]);
        }
    }
}
