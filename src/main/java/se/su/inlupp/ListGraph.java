package se.su.inlupp;

import java.util.*;

public class ListGraph<T> implements Graph<T> {
  private Map<T, Set<Edge<T>>> adjacencyList = new HashMap<>();

  @Override
  public void add(T node) {
    adjacencyList.putIfAbsent(node, new HashSet<>());
  }

  @Override
  public void connect(T node1, T node2, String name, int weight) {
    if(getEdgeBetween(node1, node2) != null){
      throw new IllegalStateException("Noderna är redan kopplade");
    }
    if(weight < 0) throw new IllegalArgumentException("Vikt får ej vara negativ");
    EdgeClass<T> newEdge1to2 = new EdgeClass<>(node2, name, weight);
    EdgeClass<T> newEdge2to1 = new EdgeClass<>(node1, name, weight);
    adjacencyList.get(node1).add(newEdge1to2);
    adjacencyList.get(node2).add(newEdge2to1);
  }

  @Override
  public void setConnectionWeight(T node1, T node2, int weight) {
    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);
    if(edge1 == null || edge2 == null) throw new NoSuchElementException("Nodena har ingen koppling");
    if(weight < 0) throw new IllegalArgumentException("Vikt får ej vara negativ");
    edge1.setWeight(weight);
    edge2.setWeight(weight);
  }

  @Override
  public Set<T> getNodes() {
    Set<T> copyOfNodes = new HashSet<>(adjacencyList.keySet());
    return copyOfNodes;
  }

  @Override
  public Collection<Edge<T>> getEdgesFrom(T node) {
    if(!adjacencyList.containsKey(node)) throw new NoSuchElementException(node + " finns ej i grafen");
    Set<Edge<T>> copyOfNodeSet = new HashSet<>(adjacencyList.get(node));
    return copyOfNodeSet;
  }

  @Override
  public Edge<T> getEdgeBetween(T node1, T node2) {
    if(!adjacencyList.containsKey(node1)) throw new NoSuchElementException(node1 + " finns ej i grafen");
    if(!adjacencyList.containsKey(node2)) throw new NoSuchElementException(node2 + " finns ej i grafen");
    for(Edge<T> edge : adjacencyList.get(node1)){
      if(edge.getDestination().equals(node2)) return edge;
    }
    return null;
  }

  @Override
  public void disconnect(T node1, T node2) {
    Edge<T> edge1 = getEdgeBetween(node1, node2);
    Edge<T> edge2 = getEdgeBetween(node2, node1);
    if(edge1 == null || edge2 == null) throw new IllegalStateException("Noderna har ingen koppling");
    adjacencyList.get(node1).remove(edge1);
    adjacencyList.get(node2).remove(edge2);
  }

  @Override
  public void remove(T node) {
    if(!adjacencyList.containsKey(node)) throw new NoSuchElementException(node + " finns ej i grafen");
    for(Edge<T> edge: getEdgesFrom(node)){
      T connectedNode = edge.getDestination();
      disconnect(connectedNode, node);
    }
    adjacencyList.remove(node);
  }

  @Override
  public boolean pathExists(T from, T to) {
    if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
      return false;

    }
    Set<T> visited = new HashSet<>();
    return dfs(from, to, visited);
  }

  @Override
  public List<Edge<T>> getPath(T from, T to) {
    if (!adjacencyList.containsKey(from) || !adjacencyList.containsKey(to)) {
      throw new NoSuchElementException("En eller båda noderna finns inte i grafen");
    }

    Set<T> visited = new HashSet<>();
    List<Edge<T>> path = new ArrayList<>();

    boolean found = findPath(from, to, visited, path);
    return found ? path : null;
  }

  @Override
  public String toString(){
    return adjacencyList.toString();
  }

  private boolean dfs(T current, T target, Set<T> visited){
    if(current.equals(target)) return true;

    visited.add(current);

    for(Edge<T> edge : getEdgesFrom(current)){
      T connectedNode = edge.getDestination();
      if(!visited.contains(connectedNode)){
        if(dfs(connectedNode, target, visited)){
          return true;
        }
      }
    }
    return false;
  }

  private boolean findPath(T current, T target, Set<T> visited, List<Edge<T>> path){
    if(current.equals(target)){
      return true;
    }

    visited.add(current);

    for(Edge<T> edge : getEdgesFrom(current)){
      T connectedNode = edge.getDestination();

      if(!visited.contains(connectedNode)){
        path.add(edge);

        if(findPath(connectedNode, target, visited, path)){
          return true;
        }

        path.remove(path.size() - 1);
      }
    }
    return false;
  }

}
