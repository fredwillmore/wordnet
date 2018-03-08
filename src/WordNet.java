import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

public class WordNet {

    private final Digraph G;
    private final Words words;
    private final SAP sap;

    private class Words {
        private final Map<String, ArrayList<Integer>> idsLookup = new HashMap<String, ArrayList<Integer>>();
        private final Map<Integer, ArrayList<String>> nounsLookup = new HashMap<Integer, ArrayList<String>>();

        public Words(In in) {
            String[] lines = in.readAllLines();
            for (int i = 0; i < lines.length; i++) {
                String id = lines[i].split(",")[0], nouns = lines[i].split(",")[1];
                for(String noun: nouns.split(" ")) {
                    if(idsLookup.get(noun) == null) {
                        idsLookup.put(noun, new ArrayList<Integer>());
                    }
                    idsLookup.get(noun).add(i);

                    if(nounsLookup.get(i) == null) {
                        nounsLookup.put(i, new ArrayList<String>());
                    }
                    nounsLookup.get(i).add(noun);
                }
            }
        }

        public ArrayList<Integer> findAll(String noun) {
            return idsLookup.get(noun);
        }

        public int size() {
            return idsLookup.size();
        }

        public Iterable<String> getNouns() {
            return idsLookup.keySet();
        }

        public Iterable<String> getNouns(int id) {
            return nounsLookup.get(id);
        }

        public String getSynset(int id) {
            return String.join(" ", nounsLookup.get(id));
        }

        public boolean contains(String noun) {
            return idsLookup.get(noun) != null;
        }

        public ArrayList<Integer> getIDs(String noun) {
            return idsLookup.get(noun);
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
            for(int i = 1; i < values.length; i++) {
                int w = Integer.parseInt(values[i]);
                G.addEdge(v, w);
            }
        }
        if (!isRootedDag(G)) {
            throw new java.lang.IllegalArgumentException();
        }
        sap = new SAP(G);
    }

    private boolean isRootedDag(Digraph G) {
        return isRooted(G) && isDag(G);
    }

    private boolean isRooted(Digraph G) {
        int roots = 0;
        for(int v = 0; v < G.V(); v++) {
            if(G.outdegree(v) == 0 && G.indegree(v) > 0)
                roots++;
        }
        return roots == 1;
    }

    private boolean isDag(Digraph G) {
        return !(new DirectedCycle(G).hasCycle());
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
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();

        return sap.length(words.findAll(nounA), words.findAll(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();

        int ancestor = sap.ancestor(words.findAll(nounA), words.findAll(nounB));
        return words.getSynset(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        System.out.println("testConstructor: " + testConstructor("synsets.txt", "hypernyms.txt"));
        System.out.println("testNouns: " + testNouns());
        System.out.println("testIsNoun: " + testIsNoun());
        System.out.println("testDistance: " + testDistance());
        System.out.println("testSap: " + testSap());
        System.out.println("testInvalid: " + testInvalid());
    }

    private static boolean testConstructor(String synsetsFile, String hypernymsFile) {
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        System.out.println("# vertices: " + wordnet.G.V());
        System.out.println("# edges: " + wordnet.G.E());
        return wordnet.getClass().getName().equals("WordNet");
    }

    private static boolean testNouns() {
        String synsetsFile = "synsets15.txt";
        String hypernymsFile = "hypernyms15Path.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        System.out.println(wordnet.nouns().toString());
        return wordnet.nouns().toString().equals("[a, b, c, d, e, f, g, h, i, j, k, l, m, n, o]");
    }

    private static boolean testIsNoun() {
        String synsetsFile = "synsets.txt";
        String hypernymsFile = "hypernyms.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        return wordnet.isNoun("military_rank") && !wordnet.isNoun("yar");
    }

    private static boolean testDistance() {
        String synsetsFile = "synsets.txt";
        String hypernymsFile = "hypernyms.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);

        boolean test1 = wordnet.distance("horror", "stinker") == 2;
        boolean test2 = wordnet.distance("thing", "thing") == 0;
        boolean test3 = wordnet.distance("military_rank", "fastening") == 13;

        return test1 && test2 && test3;
    }

    private static boolean testSap() {
        String synsetsFile = "synsets.txt";
        String hypernymsFile = "hypernyms.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);

        boolean test1 = wordnet.sap("aminopherase", "filaggrin").equals("protein");
        boolean test2 = wordnet.sap("transferase", "IgE").equals("protein");
        boolean test3 = wordnet.sap("beta_globulin", "plasma_protein").equals("protein");
        boolean test4 = wordnet.sap("urease", "ferritin").equals("protein");
        boolean test5 = wordnet.sap("factor_I", "unit").equals("unit building_block");
        boolean test6 = wordnet.sap("freshener", "change").equals("thing");
        boolean test7 = wordnet.sap("human_gamma_globulin", "haematohiston").equals("simple_protein");
        boolean test8 = wordnet.sap("horror", "stinker").equals("thing");
        boolean test9 = wordnet.sap("thing", "thing").equals("matter affair thing");
        boolean test10 = wordnet.sap("military_rank", "fastening").equals("abstraction abstract_entity");

        return test1 && test2 && test3 && test4 && test5 && test6 && test7 && test8 && test9 && test10;
    }

    private static boolean testInvalid() {
        String synsetsFile = "synsets3.txt";
        String hypernymsFile = "hypernyms3InvalidTwoRoots.txt";
        try {
            WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        } catch (IllegalArgumentException e) {
            return true;
        }
        return false;
    }

}
