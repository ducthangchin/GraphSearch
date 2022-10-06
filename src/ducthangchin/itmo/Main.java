package ducthangchin.itmo;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import ducthangchin.itmo.*;

class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Graph graph = new Graph("./src/resources/data.txt");

        String start = "Рига";
        String end = "Уфа";

        System.out.println("Search with BFS:");
        graph.BFS(start, end);
        System.out.println();

        System.out.println("Search with DFS:");
        graph.DFS(start, end);
        System.out.println();

        System.out.println("Search with DLS:");
        graph.DLS(start, end, 10);
        System.out.println();

        System.out.println("Search with IDDLS:");
        graph.IDDLS(start, end, 20);
        System.out.println();

        System.out.println("Search with Bidirectional search:");
        graph.biDirSearch(start, end);
        System.out.println();

        System.out.println("Search with Greedy Best-first Search:");
        graph.GBFS(start, end);
        System.out.println();

        System.out.println("Search with A star:");
        graph.AStar(start, end);
        System.out.println();

    }


}