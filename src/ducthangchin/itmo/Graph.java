package ducthangchin.itmo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

class Graph
{
    private int V;
    private List<String> vertices;
    private LinkedList<Integer> adj[];

    private List<Heuristic> heuristics;

    private int[][] weights;

    public Graph(String path) throws FileNotFoundException {
        File myObj = new File(path);
        Scanner myReader = new Scanner(myObj);

        vertices = new ArrayList<>();
        List<int[]> edges = new ArrayList<>();
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String[] tmp = data.split("\\s+");

            int pos1, pos2;
            pos1 = vertices.indexOf(tmp[0]);
            pos2 = vertices.indexOf(tmp[1]);

            if (pos1 == -1) {
                vertices.add(tmp[0]);
                pos1 = vertices.size() - 1;
            }

            if (pos2 == -1) {
                vertices.add(tmp[1]);
                pos2 = vertices.size() - 1;
            }

            int[] edge = {pos1, pos2, Integer.parseInt(tmp[2])};
            edges.add(edge);
        }

        V = vertices.size();
        adj = new LinkedList[V];
        for (int i = 0; i < V; ++i)
            adj[i] = new LinkedList();

        weights = new int[V][V];

        edges.stream().forEach(e -> {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
            weights[e[0]][e[1]] = e[2];
            weights[e[1]][e[0]] = e[2];
        });

        setHeuristics();


    }

    public void setHeuristics() throws FileNotFoundException {
        heuristics = new ArrayList<>();
        Scanner scanner = new Scanner(new File("./src/resources/distanceToUfa.txt"));
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            String[] tmp = data.split("\\s+");
            Heuristic heuristic = new Heuristic(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
            heuristics.add(heuristic);
        }
    }


    // Uninformed search

    public void BFS(String start, String end) {
        int s = vertices.indexOf(start);
        int e = vertices.indexOf(end);

        List<Integer> trace = new ArrayList<>();
        boolean[] visited = new boolean[V];

        LinkedList<Integer> queue = new LinkedList<>();

        visited[s]=true;
        trace.add(s);
        queue.add(s);

        while (queue.size() != 0) {
            s = queue.poll();
            Iterator<Integer> i = adj[s].listIterator();
            while (i.hasNext()) {
                int n = i.next();
                if (!visited[n]) {
                    visited[n] = true;
                    trace.add(n);
                    queue.add(n);
                }
            }
        }

       for (int c : trace) {
           if (c != e) {
               System.out.print(vertices.get(c) + " -> ");
           }
           else break;
       }
       System.out.println(vertices.get(e));
    }


    public void DFS(String start, String end) {
        int s = vertices.indexOf(start);
        int e = vertices.indexOf(end);
        List<Integer> trace = new ArrayList<>();
        boolean[] visited = new boolean[V];

        Stack<Integer> stack = new Stack<>();
        stack.add(s);

        while (!stack.isEmpty()) {
            int tmp = stack.pop();
            if (!visited[tmp]) {
                trace.add(tmp);
                visited[tmp] = true;
                for (int c : adj[tmp]) {
                    if (!visited[c]) {
                        stack.add(c);
                    }
                }
            }
        }

        for (int c : trace) {
            if (c != e) {
                System.out.print(vertices.get(c) + " -> ");
            }
            else break;
        }
        System.out.println(vertices.get(e));
    }

    public boolean DLS(String start, String end, int limit) {
        int s = vertices.indexOf(start);
        int e = vertices.indexOf(end);

        List<Integer> trace = new ArrayList<>();
        boolean[] visited = new boolean[V];

        Stack<Integer> nodes = new Stack<>();
        Stack<Integer> depths = new Stack<>();
        nodes.add(s);
        depths.add(0);

        while (!nodes.isEmpty()) {
            int currNode = nodes.pop();
            int currDepth = depths.pop();

            if (!visited[currNode] && currDepth <= limit) {
                trace.add(currNode);
                visited[currNode] = true;

                List<Integer> edges = new ArrayList<>(adj[currNode]);
                List<Integer> tmp = new ArrayList<>();
                int t = edges.size();
                int k = 1117;
                for (int i = 0; i < t; i++) {
                    tmp.add(edges.get((i + k) % t));
                }

                for (int c : tmp) {
                    if (!visited[c] && currDepth < limit) {
                        nodes.add(c);
                        depths.add(currDepth + 1);
                    }
                }
            }
        }

        System.out.println("limit " + limit);
        if (!trace.contains(e)) {
            System.out.println("No routes from " + vertices.get(s) + " to " + vertices.get(e) + " found!");
            System.out.println("Traversal: " +
                    trace.stream()
                            .map(p -> vertices.get(p))
                            .collect(Collectors.joining(" -> "))
            );
        }
        else {
            for (int c : trace) {
                if (c != e) {
                    System.out.print(vertices.get(c) + " -> ");
                } else break;
            }
            System.out.println(vertices.get(e));
        }

        return trace.contains(e);
    }

    public boolean IDDLS (String start, String end, int maxDepth) {
        for (int i = 0; i <= maxDepth; i++) {
            if (DLS(start, end, i))
                return true;
        }
        return false;
    }

    private void bfs(Queue<Integer> queue, boolean[] visited, List<Integer> trace) {
        int current = queue.poll();
        if (!visited[current]) {
            visited[current] = true;
            trace.add(current);
            for(int i : adj[current]) {
                if (!visited[i]) {
                    queue.add(i);
                }
            }
        }

    }

    public int isIntersecting(boolean[] s_visited,
                              boolean[] t_visited)
    {
        for (int i = 0; i < V; i++) {
            // if a vertex is visited by both front
            // and back BFS search return that node
            // else return -1
            if (s_visited[i] && t_visited[i])
                return i;
        }
        return -1;
    }

    public boolean biDirSearch(String start, String end) {
        int s = vertices.indexOf(start);
        int e = vertices.indexOf(end);

        boolean[] s_visited = new boolean[V];
        boolean[] e_visited = new boolean[V];

        Queue<Integer> s_queue = new LinkedList<>();
        Queue<Integer> e_queue = new LinkedList<>();

        List<Integer> s_trace = new ArrayList<>();
        List<Integer> e_trace = new ArrayList<>();


        int intersectNode = -1;
        s_queue.add(s);
        e_queue.add(e);

        while (!s_queue.isEmpty() && !e_queue.isEmpty()) {
            bfs(s_queue, s_visited, s_trace);
            bfs(e_queue, e_visited, e_trace);

            intersectNode = isIntersecting(s_visited, e_visited);

            if (intersectNode != -1) {
                System.out.println("Intersection: " + vertices.get(intersectNode));
                System.out.println("Traversal from start: ");
                System.out.println(s_trace.stream()
                        .map(p -> vertices.get(p))
                        .collect(Collectors.joining(" -> "))
                );
                System.out.println("Traversal from goal: ");
                System.out.println(e_trace.stream()
                        .map(p -> vertices.get(p))
                        .collect(Collectors.joining(" -> "))
                );
                return true;
            }

        }

        return false;
    }

    // Informed search

    // greedy best-first search
    public void GBFS(String start, String end) {
        int s = vertices.indexOf(start);
        int e = vertices.indexOf(end);

        List<Integer> trace = new ArrayList<>();
        boolean[] visited = new boolean[V];

        PriorityQueue<Heuristic> queue = new PriorityQueue<>();
        queue.add(heuristics.get(s));


        while (!queue.isEmpty()) {
            Heuristic tmp = queue.poll();
            trace.add(tmp.getNode());

            if (!visited[tmp.getNode()]) {
                visited[tmp.getNode()] = true;
                for (int i : adj[tmp.getNode()]) {
                    if (!visited[i]) {
                        queue.add(heuristics.get(i));
                    }
                }
            }
        }

        if (!trace.contains(e)) {
            System.out.println("No routes from " + vertices.get(s) + " to " + vertices.get(e) + " found!");
            System.out.println("Traversal: " +
                    trace.stream()
                            .map(p -> vertices.get(p))
                            .collect(Collectors.joining(" -> "))
            );
        }
        else {
            for (int c : trace) {
                if (c != e) {
                    System.out.print(vertices.get(c) + " -> ");
                } else break;
            }
            System.out.println(vertices.get(e));
        }
    }

    public void getHeuristics() {
        heuristics.stream().map(p -> vertices.get(p.getNode()) + ": " + p.getCost()).forEach(System.out::println);
    }

    public void AStar(String start, String end) {
        int s = vertices.indexOf(start);
        int e = vertices.indexOf(end);

        PriorityQueue<Heuristic> queue = new PriorityQueue<>();
        boolean[] visited = new boolean[V];
        List<Integer> trace = new ArrayList<>();

        Heuristic root = heuristics.get(s);
        root.setActualCost(0);
        queue.add(root);

        while (!queue.isEmpty()) {
            Heuristic tmp = queue.poll();


            if (!visited[tmp.getNode()]) {
                trace.add(tmp.getNode());
                visited[tmp.getNode()] = true;
                for (int i : adj[tmp.getNode()]) {

                    if (!visited[i]) {
                        Heuristic child = heuristics.get(i);
                        child.setActualCost(tmp.getActualCost() + weights[tmp.getNode()][i]);
                        queue.add(child);
                    }
                }

                System.out.println(queue.stream().map(h -> vertices.get(h.getNode()) + " " + (h.getActualCost()+h.getCost())).collect(Collectors.joining("   ")));
            }
            if (visited[e]) {
                System.out.println(trace.stream().map(p -> vertices.get(p)).collect(Collectors.joining(" -> ")));
                return;
            }

        }

        trace.stream().map(p -> vertices.get(p)).forEach(System.out::println);
    }
}