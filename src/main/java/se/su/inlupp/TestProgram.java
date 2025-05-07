package se.su.inlupp;

import se.su.ovning4.Exercise4;
import se.su.ovning4.Node;
import se.su.ovning4.Person;
import se.su.ovning4.Record;

import java.util.Collection;

public class TestProgram {

    public static void main(String[] args){
        Exercise4 exempel = new Exercise4();
        exempel.loadLocationGraph("C:\\Users\\willi\\Desktop\\Skola\\prog2-ovning4\\test_data\\ex4location.graph");


          /*public SortedMap<Integer, SortedSet<Record>> getAlsoLiked(Record item) {
        System.out.println("Looking for recommendations based on: " + item);
        Collection<Edge<Node>> soldRecordings = graph.getEdgesFrom(item);
        Map<Record, Integer> popularityMap = new HashMap<>();

        for (Edge<Node> edge : soldRecordings) {
            if (edge.getDestination() instanceof Person) {
                Person person = (Person) edge.getDestination();
                System.out.println("Found owner: " + person);
                Set<Record> uniqueRecordings = new HashSet<>();

                for (Edge<Node> edge2 : graph.getEdgesFrom(person)) {
                    if (edge2.getDestination() instanceof Record record && !record.equals(item)) {
                        System.out.println("  Owns: " + record);
                        uniqueRecordings.add(record);
                    }
                }

                for (Record recording : uniqueRecordings) {
                    if (popularityMap.containsKey(recording)) {
                        int current = popularityMap.get(recording);
                        popularityMap.put(recording, current + 1);
                    } else {
                        popularityMap.put(recording, 1);
                    }
                }
            }
        }
        System.out.println("Built popularity map:");

        SortedMap<Integer, SortedSet<Record>> result = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Record, Integer> entry : popularityMap.entrySet()) {
            Record recording = entry.getKey();
            Integer popularity = entry.getValue();
            result.putIfAbsent(popularity, new TreeSet<>(Comparator.comparing(Record::getName)));
            result.get(popularity).add(recording);
        }
        System.out.println("Returning result:" + result);

        return result;
    }*/
    }
}
