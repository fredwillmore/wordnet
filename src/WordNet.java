import java.util.Iterator;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class WordNet {

    private Digraph G;
    private Words words;

    private class Words {
        private String[] words;
        public Words(In in) {
            this.words = in.readAllLines();
        }

        public int size() {
            return words.length;
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
    }

    // returns all WordNet nouns
    public Iterable<String> nouns(){
        // TODO: this is stubbed
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return null;
            }
        };
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new java.lang.IllegalArgumentException();
        // TODO: this is stubbed
        return false;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new java.lang.IllegalArgumentException();
        // TODO: this is stubbed
        return 0;
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
    }

    private static boolean testConstructor(String synsetsFile, String hypernymsFile) {
        WordNet thinger = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        System.out.println("# vertices: " + thinger.G.V());
        System.out.println("# edges: " + thinger.G.E());
        return thinger.getClass().getName() == "WordNet";
    }
}
