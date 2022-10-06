package ducthangchin.itmo;

public class Heuristic implements Comparable<Heuristic>{
    private int node;
    private int cost;

    private int actualCost;



    public Heuristic(int node, int cost) {
        this.node = node;
        this.cost = cost;
        actualCost = 0;
    }

    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getActualCost() {
        return actualCost;
    }

    public void setActualCost(int actualCost) {
        this.actualCost = actualCost;
    }


    @Override
    public int compareTo(Heuristic o) {
        if (actualCost + cost < o.getActualCost() + o.getCost())
            return -1;
        else return 1;
    }
}
