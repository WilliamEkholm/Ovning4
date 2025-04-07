package se.su.ovning4;

public class Person implements Node{
    private final String name;

    public Person(String name){
        this.name = name;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String toString(){
        return name;
    }
}
