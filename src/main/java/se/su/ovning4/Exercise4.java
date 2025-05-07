package se.su.ovning4;
import se.su.inlupp.Edge;
import se.su.inlupp.Graph;
import se.su.inlupp.ListGraph;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Exercise4 {
    private Graph<Node> graph = new ListGraph<>();

    public void loadLocationGraph(String fileName){
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            String firstLine = reader.readLine();
            String[] locations = firstLine.trim().split(";");

            Map<String, Location> nameToLocation = new HashMap<>();

            for(int i = 0; i < locations.length; i+=3){
                String name = locations[i];
                double x = Double.parseDouble(locations[i+1]);
                double y = Double.parseDouble(locations[i+2]);
                Location location = new Location(name, x, y);
                graph.add(location);
                nameToLocation.put(name, location);
            }

            String line;
            while((line = reader.readLine()) != null){
                String[] edges = line.trim().split(";");
                String fromName = edges[0];
                String toName = edges[1];
                String edgeName = edges[2];
                int weight = Integer.parseInt(edges[3]);

                Location from = nameToLocation.get(fromName);
                Location to = nameToLocation.get(toName);

                graph.connect(from, to, edgeName, weight);
            }
            System.out.println(graph);

        }catch (IOException e){
            System.out.println("File not found" + e.getMessage());
        }
    }

    public SortedMap<Integer, SortedSet<Record>> getAlsoLiked(Record item) {
        Collection<Edge<Node>> soldRecordings = graph.getEdgesFrom(item);
        Map<Record, Integer> popularityMap = new HashMap<>();

        // Step 1: Collect all unique people who own the record
        Set<Person> owners = new HashSet<>();
        for (Edge<Node> edge : soldRecordings) {
            if (edge.getDestination() instanceof Person person) {
                owners.add(person);
            }
        }

        // Step 2: For each person, check their other records (not the original one)
        for (Person person : owners) {
            Collection<Edge<Node>> personEdges = graph.getEdgesFrom(person);
            for (Edge<Node> edge : personEdges) {
                if (edge.getDestination() instanceof Record record && !record.equals(item)) {
                    popularityMap.put(record, popularityMap.getOrDefault(record, 0) + 1);
                }
            }
        }

        // Step 3: Build the result using a TreeMap with reverse order for popularity counts
        SortedMap<Integer, SortedSet<Record>> result = new TreeMap<>(Collections.reverseOrder());

        // Use a proper comparator for Record objects
        Comparator<Record> recordComparator = Comparator
                .comparing(Record::getName)
                .thenComparing(Record::getArtist);

        // Fill the result map
        for (Map.Entry<Record, Integer> entry : popularityMap.entrySet()) {
            Record record = entry.getKey();
            Integer popularity = entry.getValue();

            // Create a new TreeSet if needed
            result.computeIfAbsent(popularity, k -> new TreeSet<>(recordComparator));
            result.get(popularity).add(record);
        }

        return result;
    }


    public int getPopularity(Record item) {
       Collection<Edge<Node>> soldRecordings = graph.getEdgesFrom(item);
       return soldRecordings.size();
    }

    public SortedMap<Integer, Set<Record>> getTop5() {
        Map<Record, Integer> recordings = new HashMap<>();
        Set<Node> nodes = graph.getNodes();
        for(Node node : nodes){
            if(node instanceof Record) {
                Record recording = (Record) node;
                int popularity = getPopularity(recording);
                recordings.put(recording, popularity);
            }
        }
        SortedMap<Integer, Set<Record>> recordingsByPopularity = new TreeMap<>(Collections.reverseOrder());
        for(Map.Entry<Record, Integer> entry : recordings.entrySet()){
            Record recording = entry.getKey();
            Integer popularity = entry.getValue();

            recordingsByPopularity.putIfAbsent(popularity, new HashSet<>());
            recordingsByPopularity.get(popularity).add(recording);
        }
        SortedMap<Integer, Set<Record>> topFive = new TreeMap<>(Collections.reverseOrder());
        int levelsOfPopularity = 0;


        for(Map.Entry<Integer, Set<Record>> entry : recordingsByPopularity.entrySet()){
            if(levelsOfPopularity == 5) break;
            topFive.put(entry.getKey(), entry.getValue());
                levelsOfPopularity++;
        }
       return topFive;
    }

    public void loadRecommendationGraph(String fileName) {
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            Map<String, Person> persons = new HashMap<>();
            Map<String, Record> recordings = new HashMap<>();

            String line;
            while((line = reader.readLine()) != null){
                String[] lineParts = line.trim().split(";");
                String personName = lineParts[0];
                String title = lineParts[1];
                String artistName = lineParts[2];

                Person person;
                if(!persons.containsKey(personName)){
                    person = new Person(personName);
                    graph.add(person);
                    persons.put(personName, person);
                } else{
                    person = persons.get(personName);
                }

                String recordKey = title + "//" + artistName;
                Record recording;
                if(!recordings.containsKey(recordKey)){
                    recording = new Record(title, artistName);
                    graph.add(recording);
                    recordings.put(recordKey, recording);
                } else{
                    recording = recordings.get(recordKey);
                }

                graph.connect(person, recording, "owns", 1);
            }

        } catch(IOException e){
            System.out.println("File not found " + e.getMessage());
        }

    }

}
