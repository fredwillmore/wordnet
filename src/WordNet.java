import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;


public class WordNet {

    private Digraph G;
    private Digraph reverseG;
    private Words words;

    private class Words {
        private ArrayList<String> nouns = new ArrayList<String>();
        private ArrayList<String> glosses = new ArrayList<String>();

        public Words(In in) {
            String[] lines = in.readAllLines();
            for (int i = 0; i < lines.length; i++) {
                String[] line = lines[i].split(",");
                if(i != Integer.parseInt(line[0])) {
                    System.out.println("oh no! this shouldn't happen! maybe my inputs are garbage, or maybe I shouldn't rely on add order for indexing");
//                    throw new Exception(); // TODO: what kind of exception
                }
                nouns.add(line[1]);
                glosses.add(line[2]);
            }
        }

        public Iterable<Integer> findAll(String noun) {
            ArrayList<Integer> indices = new ArrayList<Integer>();
            for (int i=0; i<nouns.size(); i++)
                if(nouns.get(i).equals(noun))
                    indices.add(i);
            return indices;
        }

        public int size() {
            return nouns.size();
        }

        public Iterable<String> getNouns() {
            return nouns;
        }

        public boolean contains(String noun) {
            return nouns.contains(noun);
        }
    }

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new java.lang.IllegalArgumentException();

        words = new Words(new In(synsets));
        G = new Digraph(words.size());
        for (String line: new In(hypernyms).readAllLines()) {
            String[] values = line.split(",");
            int v = Integer.parseInt(values[0]);
            int w = Integer.parseInt(values[1]);
            G.addEdge(v, w);
        }
        reverseG = G.reverse();
    }

    // returns all WordNet nouns
    public Iterable<String> nouns(){
        return words.getNouns();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new java.lang.IllegalArgumentException();
        return words.contains(word);
    }

    // distance is the minimum length of any ancestral path between any synset v of A and any synset w of B.
    public int distance(String nounA, String nounB) {
        Iterable<Integer> synsetsA = words.findAll(nounA);
        Iterable<Integer> synsetsB = words.findAll(nounB);

        if (nounA == null || nounB == null)
            throw new java.lang.IllegalArgumentException();

        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(G, synsetsA);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(G, synsetsB);

        // find ancestors of nounA - obvious candidate for refactoring
        ArrayList<Integer> ancestors = new ArrayList<Integer>();
        for (int v = 0; v < words.size(); v++) {
            if(bfsA.hasPathTo(v))
                ancestors.add(v);
        }

        // find ancestors of nounB
        ArrayList<Integer> ancestorsB = new ArrayList<Integer>();
        for (int v = 0; v < words.size(); v++) {
            if(bfsB.hasPathTo(v))
                ancestorsB.add(v);
        }

        ancestors.retainAll(ancestorsB);

        Integer distance = Integer.MAX_VALUE;
        for (int ancestor: ancestors) {
            int d = bfsA.distTo(ancestor) + bfsB.distTo(ancestor);
            if (d < distance)
                distance = d;
        }

        return distance; // still stubbed
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new java.lang.IllegalArgumentException();
        // TODO: this is stubbed
        return "I am not here";
    }

    // do unit testing of this class
    public static void main(String[] args) {
        System.out.println("testConstructor: " + testConstructor("synsets3.txt", "hypernyms3InvalidCycle.txt"));
        System.out.println("testNouns: " + testNouns());
        System.out.println("testIsNoun: " + testIsNoun());
        System.out.println("testDistance: " + testDistance());
    }

    private static boolean testConstructor(String synsetsFile, String hypernymsFile) {
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        System.out.println("# vertices: " + wordnet.G.V());
        System.out.println("# edges: " + wordnet.G.E());
        return wordnet.getClass().getName() == "WordNet";
    }

    private static boolean testNouns() {
        String synsetsFile = "synsets15.txt";
        String hypernymsFile = "hypernyms15Path.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        System.out.println(wordnet.nouns().toString());
        return wordnet.nouns().toString().equals("[a, b, c, d, e, f, g, h, i, j, k, l, m, n, o]");
    }

    private static boolean testIsNoun() {
        String synsetsFile = "synsets15.txt";
        String hypernymsFile = "hypernyms15Path.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        return wordnet.isNoun("a") && !wordnet.isNoun("x");
    }

    private static boolean testDistance() {
        String synsetsFile = "synsets100-subgraph.txt";
        String hypernymsFile = "hypernyms100-subgraph.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        boolean test1 = wordnet.distance("horror", "stinker") == 2;
        boolean test2 = wordnet.distance("thing", "thing") == 0;
        return test1 && test2;
    }

}
