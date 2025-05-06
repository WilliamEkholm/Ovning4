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
       return null;
    }

    public int getPopularity(Record item) {
       Collection<Edge<Node>> soldRecordings = graph.getEdgesFrom(item);
       return soldRecordings.size();
    }

    public SortedMap<Integer, Set<Record>> getTop5() {
       return null;
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
                Record recording;
                if(!recordings.containsKey(title)){
                    recording = new Record(title, artistName);
                    graph.add(recording);
                    recordings.put(title, recording);
                } else{
                    recording = recordings.get(title);
                }

                graph.connect(person, recording, "owns", 1);
            }

        } catch(IOException e){
            System.out.println("File not found " + e.getMessage());
        }

    }

}
