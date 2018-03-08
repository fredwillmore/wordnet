
public class Outcast {

    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int greatest = 0;
        String outcast = "";
        for (String nounMain: nouns) {
            int sum = 0;
            for (String nounOther: nouns) {
                if (!nounOther.equals(nounMain)) {
                    sum += wordnet.distance(nounMain, nounOther);
                }
            }
            if (sum > greatest) {
                greatest = sum;
                outcast = nounMain;
            }
        }
        return outcast;
    }

    // see test client below
    public static void main(String[] args) {
        System.out.println("testConstructor: " + testConstructor());
        System.out.println("testOutcast: " + testOutcast());
        System.out.println("testOutcast1: " + testOutcast1());
        System.out.println("testOutcast2: " + testOutcast2());
        System.out.println("testOutcast3: " + testOutcast3());
    }

    private static boolean testConstructor() {
        String synsetsFile = "synsets100-subgraph.txt";
        String hypernymsFile = "hypernyms100-subgraph.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        Outcast outcast = new Outcast(wordnet);
        return outcast.getClass().getName().equals("Outcast");
    }
    private static boolean testOutcast() {
        String synsetsFile = "synsets100-subgraph.txt";
        String hypernymsFile = "hypernyms100-subgraph.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        Outcast outcast = new Outcast(wordnet);
        return outcast.outcast(new String[] {
                "human_gamma_globulin",
                "protein",
                "thing",
                "factor_XI"
        }).equals("thing");
    }

    private static boolean testOutcast1() {
        String synsetsFile = "synsets.txt";
        String hypernymsFile = "hypernyms.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        Outcast outcast = new Outcast(wordnet);
        return outcast.outcast(new String[] {
                "probability", "statistics", "mathematics", "physics"
        }).equals("probability");
    }

    private static boolean testOutcast2() {
        String synsetsFile = "synsets.txt";
        String hypernymsFile = "hypernyms.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        Outcast outcast = new Outcast(wordnet);
        return outcast.outcast(new String[] {
                "horse", "zebra", "cat", "bear", "table"
        }).equals("table");
    }

    private static boolean testOutcast3() {
        String synsetsFile = "synsets.txt";
        String hypernymsFile = "hypernyms.txt";
        WordNet wordnet = new WordNet("wordnet/" + synsetsFile, "wordnet/" + hypernymsFile);
        Outcast outcast = new Outcast(wordnet);
        return outcast.outcast(new String[] {
                "Turing", "von_Neumann", "Mickey_Mouse"
        }).equals("Mickey_Mouse");
    }
}
