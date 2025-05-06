package se.su.inlupp;

public class EdgeClass<T> implements Edge<T> {
    private T destination;
    private String name;
    private int weight;

    public EdgeClass(T destination, String name, int weight) {
        this.destination = destination;
        this.name = name;
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
            if (weight < 0) {
                throw new IllegalArgumentException("Fel: vikt får ej vara negativ");
            }
        this.weight = weight;
    }

    @Override
    public T getDestination(){
        return destination;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return " till " + destination + " med " + name + " tar " + weight;
    }




}
