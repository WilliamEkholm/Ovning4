package se.su.ovning4;

import org.junit.jupiter.api.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Exercise4Test {
    //
    private static final String PREFIX = "src/test/resources/";
    private static final String UPDATED_DATE = "2024-04-17";
    //
    private static final String EX_4_LOCATION_GRAPH_FILE = Paths.get(PREFIX , "ex4location.graph").toString();
    private static final String EX_4_RECO_GRAPH_LARGE_FILE = Paths.get(PREFIX , "ex4recommendation.graph").toString();
    private static final String EX_4_RECO_GRAPH_SMALL_FILE = Paths.get(PREFIX , "ex4recosmall.graph").toString();
    private static final String RECORDS_FILE = Paths.get(PREFIX , "records.data").toString();
    private static final String EX_4_EMPTY_FILE = Paths.get(PREFIX , "empty.graph").toString();

    private static final Map<Integer, Set<String>> EXPECTED_MAP = Map.of(
            3, Set.of("The Miseducation of Lauryn Hill"),
            2, Set.of("Licensed to Ill", "Nevermind", "1999", "Kid A", "Voodoo"),
            1, Set.of("Stories From the City, Stories From the Sea", "Rust Never Sleeps", "Talking Book", "The Velvet Underground & Nico", "Automatic for the People", "Talking Heads: 77")
    );

    private static final List<String> locationNames = List.of(
            "Amsterdam", "Berlin", "Dublin", "Lisabon", "London",
            "Madrid", "Oslo", "Paris", "Stockholm", "Warszawa"
    );

    private Exercise4 sut;
    private Exercise4Oracle oracle;
    private Graph<Node> graph;
    private Graph<Node> oragraph;

    private static Set<String> names() {
        return Set.of(
                "Anna", "Maria", "Eva", "Karin", "Lena",
                "Emma", "Kerstin", "Sara", "Malin", "Ingrid",
                "Linda", "Elin", "Birgitta", "Marie", "Inger",
                "Johanna", "Hanna", "Sofia", "Annika", "Ulla",
                "Julia", "Susanne", "Jenny", "Carina", "Ida",
                "Christina", "Helena", "Åsa", "Kristina", "Camilla",
                "Lars", "Anders", "Johan", "Peter", "Jan",
                "Daniel", "Mikael", "Erik", "Per", "Fredrik",
                "Hans", "Andreas", "Stefan", "Magnus", "Mats",
                "Jonas", "Bengt", "Alexander", "Martin", "Thomas",
                "Bo", "Karl", "Nils", "Björn", "Leif",
                "David", "Emil", "Ulf", "Sven", "Simon"
        );
    }

    private static String s(String... strings) {
        return String.format("%s;%s", strings[0], strings[1]);
    }

    private static List<String> records() {
        List<String> n = new ArrayList<>();
        try {
            var lines = Files.readAllLines(Path.of(RECORDS_FILE));
            lines.stream().map(s -> s.substring(1, s.length() - 1)).map(s -> s.split("\", \"")).map(Exercise4Test::s).forEach(n::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return n;
    }

    private static void generateFile() {

        var records = records();

        var names = names();

        Map<String, Set<String>> mappings = new HashMap<>();
        Random random = new Random();
        for (String name : names) {
            var n = random.nextInt(50) + 1;
            for (int i = 0; i < n; i++) {
                mappings.computeIfAbsent(name, v -> new HashSet<>()).add(records.get(random.nextInt(500)));
            }
        }

        Charset charset = StandardCharsets.UTF_8;
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(EX_4_RECO_GRAPH_LARGE_FILE), charset))) {
            for (var entry : mappings.entrySet()) {
                for (String s : entry.getValue()) {
                    pw.printf("%s;%s%n", entry.getKey(), s);
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    @Test
    @Order(0)
    @Tag("0")
    @DisplayName("Information")
    void __version() {
        System.out.printf("Test uppdaterat %s.%n", UPDATED_DATE);
    }

    private void checkGraphIsEmpty() {
        graph.getNodes().forEach(node -> graph.remove(node));
        oragraph.getNodes().forEach(node -> oragraph.remove(node));
        assertTrue(graph.getNodes().isEmpty(), "Grafen ska vara tom när testet börjar.");
    }

    private void checkGraphIsNotEmpty() {
        assertFalse(graph.getNodes().isEmpty(), "Grafen ska inte vara tom efter att data har laddats.");
    }

    private boolean checkImplementsGetAlsoLiked() {
        if (!checkImplementsLoad())
            return false;

        checkStateAndLoadData(EX_4_RECO_GRAPH_SMALL_FILE);

        var node = graph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast)
                .filter(n -> n.getName().equals("Nevermind (Nirvana)")).findFirst();

        if (node.isPresent()) {
            try {
                var result = sut.getAlsoLiked(node.get());
                if (result == null) return false;
            } catch (NoSuchElementException e) {
                return false;
            }
        }

        return true;
    }

    private boolean checkImplementsGetPopularity() {
        if (!checkImplementsLoad())
            return false;

        checkStateAndLoadData(EX_4_RECO_GRAPH_SMALL_FILE);

        var node = graph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast)
                .filter(n -> n.getName().equals("The Miseducation of Lauryn Hill (Lauryn Hill)")).findFirst();

        if (node.isPresent()) {
            try {
                var pop = sut.getPopularity(node.get());
                if (pop == -1) return false;
            } catch (NoSuchElementException e) {
                return false;
            }
        }

        return true;
    }

    private boolean checkImplementsGetTop5() {
        return checkImplementsLoad() && sut.getTop5() != null;
    }

    private boolean checkImplementsLoad() {
        try {
            sut.loadRecommendationGraph(EX_4_EMPTY_FILE);
        } catch (UnsupportedOperationException e) {
            return false;
        }
        return true;
    }

    private void checkStateAndLoadData(String filename) {
        checkGraphIsEmpty();

        sut.loadRecommendationGraph(filename);
        oracle.loadRecommendationGraph(filename);

        checkGraphIsNotEmpty();
    }

    private void checkStateAndLoadRandomData(String filename) {
        generateFile();
        checkStateAndLoadData(filename);
    }

//    @Test
//    @DisplayName("Gränssnitt och stilkontroller")
//    @Tag("26")
//    void checkStyle() {
//        CheckstyleRunnerChecks.prog2checks();
//    }

    @Test
    @Order(10)
    @Tag("50")
    @DisplayName("Testar loadLocationGraph")
    void loadLocationGraph() {

        checkGraphIsEmpty();

        try {
            sut.loadLocationGraph(EX_4_LOCATION_GRAPH_FILE);
        } catch (NullPointerException e) {
            var msg = String.format("Ett undantag genererades i metoden loadLocationGraph:%n%s%n%s:%s", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
            fail(msg);
        }

        checkGraphIsNotEmpty();

        var result = graph.getNodes().stream()
                .map(Location.class::cast)
                .map(Location::getName)
                .sorted()
                .collect(Collectors.toList());

        assertEquals(locationNames, result, "Fel: grafen innehåller inte de förväntade noderna efter inläsning.");

        var sthml = graph.getNodes().stream()
                .map(Location.class::cast)
                .filter(locationNode -> locationNode.getName().equals("Stockholm"))
                .findFirst().orElse(null);

        assertNotNull(sthml, "Hittade inte noden 'Stockholm' i grafen.");
        assertTrue(sthml.toString().contains("470"), "Hittade inte x för noden Stockholm.");
        assertTrue(sthml.toString().contains("242"), "Hittade inte y för noden Stockholm.");

        var madrid = graph.getNodes().stream()
                .map(graphNode -> (Location) graphNode)
                .filter(locationNode -> locationNode.getName().equals("Madrid"))
                .findFirst().orElse(null);

        assertNotNull(madrid, "Hittade inte noden 'Madrid' i grafen.");
        assertTrue(madrid.toString().contains("139"), "Hittade inte x för noden Madrid.");
        assertTrue(madrid.toString().contains("555"), "Hittade inte y för noden Madrid.");

        var edges = new ArrayList<Edge<Node>>();

        for (Node node : graph.getNodes()) {
            edges.addAll(graph.getEdgesFrom(node));
        }

        assertEquals(10, graph.getNodes().size(), "Fel: grafen ska innehålla 10 noder efter att filen har lästs in.");
        assertEquals(18, edges.size(), "Fel: grafen ska innehålla 18 bågar efter att filen har lästs in.");
        assertTrue(graph.pathExists(sthml, madrid), "Fel: efter att filen har lästs in ska det finnas en path mellan Stockholm och Madrid.");
    }

    @Test
    @Order(110)
    @Tag("1")
    @DisplayName("Testar getAlsoLiked (fixerad data)")
    void getAlsoLikedFixed() {

        Assumptions.assumeTrue(checkImplementsGetAlsoLiked(), "NYI");

        checkStateAndLoadData(EX_4_RECO_GRAPH_SMALL_FILE);

        var node = graph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast)
                .filter(n -> n.toString().equals("Nevermind (Nirvana)"))
                .findFirst();

        var oranode = oragraph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast)
                .filter(n -> n.toString().equals("Nevermind (Nirvana)"))
                .findFirst();

        assertTrue(node.isPresent(), String.format("Kunde inte hitta skivan Nevermind (Nirvana)!"));

        var actual = sut.getAlsoLiked(node.get());
        var expected = oracle.getAlsoLiked(oranode.get());

            Assumptions.assumingThat(actual != null, () -> {
                assert actual != null;
                var message = String.format("Skivan %s förväntades ha %d rekommendationer, men hade %d%n."
                        , oranode.get().getName(), expected.size(), actual.size());
                assertEquals(expected.size(), actual.size(), message);

                assertTrue(actual.keySet().containsAll(expected.keySet()));
                assertEquals(expected.toString(), actual.toString());
            });
    }

    @Test
    @Order(115)
    @Tag("1")
    @DisplayName("Testar getAlsoLiked (slumpad data)")
    void getAlsoLikedRandomized() {

        Assumptions.assumeTrue(checkImplementsGetAlsoLiked(), "NIY");

        checkStateAndLoadRandomData(EX_4_RECO_GRAPH_LARGE_FILE);

        var rnode = graph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast).min(Comparator.comparing(Record::getName));

        var oranode = oragraph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast).min(Comparator.comparing(Record::getName));

        assertTrue(rnode.isPresent() && oranode.isPresent());

        if (rnode.isPresent() && oranode.isPresent()) {
            var expected = oracle.getAlsoLiked(oranode.get());
            var actual = sut.getAlsoLiked(rnode.get());

            assertEquals(expected.size(), actual.size(), "Det förväntade antalet rekommendationer stämmer inte.");

            assertTrue(actual.keySet().containsAll(expected.keySet()));

            assertEquals(expected.toString(), actual.toString());
            for (var entry : actual.entrySet()) {
                var actualRecords = entry.getValue().stream().map(Record::toString).collect(Collectors.toSet());
                var expectedRecords = actual.get(entry.getKey()).stream().map(Record::toString).collect(Collectors.toSet());

                assertEquals(expectedRecords, actualRecords, "Mappen har inte det förväntade innehållet.");
            }
        }
    }

    @Test
    @Order(120)
    @Tag("1")
    @DisplayName("Testar getPopularity (fixerad data)")
    void getPopularityFixedData() {

        Assumptions.assumeTrue(checkImplementsGetPopularity(), "NIY");

        checkStateAndLoadData(EX_4_RECO_GRAPH_SMALL_FILE);

        var n1 = graph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast)
                .filter(node -> node.toString().equals("The Miseducation of Lauryn Hill (Lauryn Hill)"))
                .findFirst();

        assertTrue(n1.isPresent(), "Skivan The Miseducation of Lauryn Hill kunde inte hittas!");

        n1.ifPresent(recordNode -> {
            var popularity = sut.getPopularity(n1.get());
            System.out.println("POPULARITY: " + popularity);
            assertEquals(3, popularity, "Fel värde för skivan av Lauryn Hill.");
        });
    }

    @Test
    @Order(125)
    @Tag("1")
    @DisplayName("Testar getPopularity (slumpad data)")
    void getPopularityRandomized() {

        Assumptions.assumeTrue(checkImplementsGetPopularity(), "NIY");

        checkStateAndLoadRandomData(EX_4_RECO_GRAPH_LARGE_FILE);

        var node = graph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast).min(Comparator.comparing(Record::getName));

        var oranode = oragraph.getNodes().stream()
                .filter(Record.class::isInstance)
                .map(Record.class::cast).min(Comparator.comparing(Record::getName));

        assertTrue(node.isPresent());

        if (node.isPresent() && oranode.isPresent()) {
            var actual = sut.getPopularity(node.get());
            var expected = oracle.getPopularity(oranode.get());
            assertEquals(expected, actual, "Fel värde.");
        }
    }

    @Test
    @Order(130)
    @Tag("5")
    @DisplayName("Testar getTop5 (fixerad data)")
    void getTop5FixedData() {

        Assumptions.assumeTrue(checkImplementsGetTop5(), "NIY");

        checkStateAndLoadData(EX_4_RECO_GRAPH_SMALL_FILE);

        var actual = sut.getTop5();
        for (var entry : actual.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            var result = value.stream()
                    .map(Record::getName)
                    .collect(Collectors.toSet())
                    .containsAll(EXPECTED_MAP.get(key));
            assertTrue(result, "Metoden returnerar inte rätt data.");
        }
    }

    @Test
    @Order(135)
    @Tag("5")
    @DisplayName("Testar getTop5 (slumpad data)")
    void getTop5Randomized() {
        Assumptions.assumeTrue(checkImplementsGetTop5(), "NYI");

        checkStateAndLoadRandomData(EX_4_RECO_GRAPH_LARGE_FILE);

        var actual = sut.getTop5();
        var expected = oracle.getTop5();

        Assumptions.assumingThat(actual != null, () -> {
            assert actual != null;
            var msg = String.format("Avbildningen som metoden returnerar innehåller inte rätt nycklar.%n");
            assertEquals(expected.keySet(), actual.keySet(), msg);
        });

        Assumptions.assumingThat(actual != null, () -> {
                    assert actual != null;

                    actual.forEach((key, value) -> {
                        Set<String> a = new HashSet<>(), e = new HashSet<>();
                        actual.get(key).stream().map(Record::getName).forEach(a::add);
                        expected.get(key).stream().map(Record::getName).forEach(e::add);
                        assertEquals(e, a, "Metoden returnerar inte rätt data.");
                    });
                }
        );
    }

    @Test
    @Order(100)
    @Tag("5")
    @DisplayName("Testar loadRecommendationGraph (fixerad data)")
    void loadRecommendationGraphFixedData() {

        Assumptions.assumeTrue(checkImplementsLoad(), "NYI");

        checkStateAndLoadData(EX_4_RECO_GRAPH_SMALL_FILE);
    }

    @Test
    @Order(105)
    @Tag("5")
    @DisplayName("Testar loadRecommendationGraph (slumpad data)")
    void loadRecommendationGraphRandomized() {

        Assumptions.assumeTrue(checkImplementsLoad(), "NYI");

        checkStateAndLoadRandomData(EX_4_RECO_GRAPH_LARGE_FILE);
    }

    @BeforeEach
    void setUp() {
        sut = new Exercise4();
        try {
            Field f = sut.getClass().getDeclaredField("graph");
            f.setAccessible(true);
            graph = (Graph)f.get(sut);
        }catch(NoSuchFieldException | IllegalAccessException e){}
        oracle = new Exercise4Oracle();
        try {
            Field f = oracle.getClass().getDeclaredField("graph");
            f.setAccessible(true);
            oragraph = (Graph)f.get(oracle);
        }catch(NoSuchFieldException | IllegalAccessException e){}

    }

    static class Exercise4Oracle{
        private Graph<Node> graph = new ListGraph<>();

        public void loadLocationGraph(String fileName) {
            var nodes = new HashMap<String, Node>();
            try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
                String line = in.readLine();
                String[] tokens = line.split(";");
                for (int i = 0; i < tokens.length; i += 3) {
                    double x = Double.parseDouble(tokens[i + 1]);
                    double y = Double.parseDouble(tokens[i + 2]);
                    Location location = new Location(tokens[i], x, y);
                    nodes.put(tokens[i], location);
                    graph.add(location);
                }
                while ((line = in.readLine()) != null) {
                    tokens = line.split(";");
                    var from = tokens[0];
                    var to = tokens[1];
                    int time = Integer.parseInt(tokens[3]);
                    if (graph.getEdgeBetween(nodes.get(from), nodes.get(to)) == null)
                        graph.connect(nodes.get(from), nodes.get(to), tokens[2], time);
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public SortedMap<Integer, SortedSet<Record>> getAlsoLiked(Record item) {
		TreeMap<Integer, SortedSet<Record>> freq = new TreeMap<>(Comparator.reverseOrder());
		for (var person : graph.getEdgesFrom(item)) {
			for (var otherRecord : graph.getEdgesFrom(person.getDestination())) {
				var count = graph.getEdgesFrom(otherRecord.getDestination()).size();
				freq.computeIfAbsent(count, k -> new TreeSet<>(Comparator.comparing(Record::toString))).add((Record) otherRecord.getDestination());
			}
		}

		return freq;

	}

    public int getPopularity(Record item) {
		return graph.getEdgesFrom(item).size();
	}

        public TreeMap<Integer, Set<Record>> getTop5() {
            TreeMap<Integer, Set<Record>> freq = new TreeMap<>(Comparator.reverseOrder());
            for (var node : graph.getNodes()) {
                if (node instanceof Record) {
                    var count = graph.getEdgesFrom(node).size();
                    freq.computeIfAbsent(count, k -> new HashSet<>()).add((Record) node);
                }
            }
            Iterator<Map.Entry<Integer, Set<Record>>> iterator = freq.entrySet().iterator();

            TreeMap<Integer, Set<Record>> top5 = new TreeMap<>(Comparator.reverseOrder());
            for (int i = 0; i < 5 && iterator.hasNext(); i++) {
                var next = iterator.next();
                top5.put(next.getKey(), next.getValue());
            }
            return top5;
        }

        public void loadRecommendationGraph(String fileName) {
		try{
			FileReader file = new FileReader(fileName);
			BufferedReader in = new BufferedReader(file);
			String line;
			while ((line = in.readLine()) != null){
				String[] tokens = line.split(";");
				Person person = new Person(tokens[0]);
				Record record = new Record(tokens[1], tokens[2]);
				graph.add(person);
				graph.add(record);
				if (graph.getEdgeBetween(person, record) == null)
					graph.connect(person, record, "", 0);
			} // while
		}catch(FileNotFoundException e){
			System.err.println("Can't open file " + fileName);
		}catch(IOException | IndexOutOfBoundsException e){
			System.err.println("Error reading " + fileName + ": " + e.getMessage());
		}
	}
    }
}
