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

    private int shotestPathAncestor(BreadthFirstDirectedPaths bfsA, BreadthFirstDirectedPaths bfsB) {
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
        int shotestPathAncestor = -1;
        for (int ancestor: ancestors) {
            int d = bfsA.distTo(ancestor) + bfsB.distTo(ancestor);
            if (d < distance) {
                distance = d;
                shotestPathAncestor = ancestor;
            }
        }
        return shotestPathAncestor;
    }
    // distance is the minimum length of any ancestral path between any synset v of A and any synset w of B.
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new java.lang.IllegalArgumentException();

        Iterable<Integer> synsetsA = words.findAll(nounA);
        Iterable<Integer> synsetsB = words.findAll(nounB);

        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(G, synsetsA);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(G, synsetsB);

        int ancestor = shotestPathAncestor(bfsA, bfsB);
        int distance = bfsA.distTo(ancestor) + bfsB.distTo(ancestor);

        return distance;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new java.lang.IllegalArgumentException();

        Iterable<Integer> synsetsA = words.findAll(nounA);
        Iterable<Integer> synsetsB = words.findAll(nounB);

        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(G, synsetsA);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(G, synsetsB);

        int ancestor = shotestPathAncestor(bfsA, bfsB);
        return words.nouns.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        System.out.println("testConstructor: " + testConstructor("synsets3.txt", "hypernyms3InvalidCycle.txt"));
        System.out.println("testNouns: " + testNouns());
        System.out.println("testIsNoun: " + testIsNoun());
        System.out.println("testDistance: " + testDistance());
        System.out.println("testSap: " + testSap());
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

    private static boolean testSap() {
        String synsetsFile = "synsets100-subgraph.txt";
        String hypernymsFile = "hypernyms100-subgraph.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);

//        for(int i=0; i<wordnet.words.size(); i++)
//            for(int j=0; j<wordnet.words.size(); j++) {
//                String w1 = wordnet.words.nouns.get(i);
//                String w2 = wordnet.words.nouns.get(j);
//                String w = wordnet.sap(w1, w2);
//                if(5 == (i*wordnet.words.size()+j)%116)
//                    System.out.println(w1 + ", " + w2 + ": " + w);
//            }

        boolean test1 = wordnet.sap("transaminase aminotransferase aminopherase", "filaggrin").equals("protein");
        boolean test2 = wordnet.sap("transferase", "immunoglobulin_E IgE").equals("protein");
        boolean test3 = wordnet.sap("transferrin beta_globulin siderophilin", "plasma_protein").equals("protein");
        boolean test4 = wordnet.sap("urease", "ferritin").equals("protein");
        boolean test5 = wordnet.sap("fibrinogen factor_I", "unit building_block").equals("unit building_block");
        boolean test6 = wordnet.sap("freshener", "change").equals("thing");
        boolean test7 = wordnet.sap("gamma_globulin human_gamma_globulin", "globin hematohiston haematohiston").equals("protein");
        boolean test8 = wordnet.sap("horror", "stinker").equals("thing");
        boolean test9 = wordnet.sap("thing", "thing").equals("thing");

        return test1 && test2 && test3 && test4 && test5 && test6 && test7 && test8 && test9;


    }

}
