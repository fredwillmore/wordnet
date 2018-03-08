import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Random;

public class SAP {
    // constructor takes a digraph (not necessarily a DAG)

    private final Digraph G;
    private static int loopCount = 0;

    public SAP(Digraph G) {
        if (G == null)
            throw new java.lang.IllegalArgumentException();
        this.G = new Digraph(G);
    }

    private BreadthFirstDirectedPaths getBFS(int v) {
        if(v < 0 || v > G.V())
            throw new java.lang.IllegalArgumentException();

        return new BreadthFirstDirectedPaths(G, v);
    }

    private BreadthFirstDirectedPaths getBFS(Iterable<Integer> v) {
        if(v == null)
            throw new java.lang.IllegalArgumentException();

        return new BreadthFirstDirectedPaths(G, v);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Bag<Integer> bv = new Bag<Integer>();
        bv.add(v);
        Bag<Integer> bw = new Bag<Integer>();
        bw.add(w);
        return length(bv, bw);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfsv = getBFS(v);
        BreadthFirstDirectedPaths bfsw = getBFS(w);
        int sapLength = -1;

        ArrayList<Integer> ancestors = findAncestors(bfsv);
        ancestors.retainAll(findAncestors(bfsw));

        for (int a: ancestors) {
            loopCount++;
            int d = bfsv.distTo(a) + bfsw.distTo(a);
            if (d < sapLength || sapLength == -1) {
                sapLength = d;
            }
        }

        return sapLength;
    }

    private ArrayList<Integer> findAncestors(BreadthFirstDirectedPaths bfs) {
        ArrayList<Integer> a = new ArrayList<Integer>();
        for (int v = 0; v < G.V(); v++) {
            if (bfs.hasPathTo(v))
                a.add(v);
        }
        return a;
    }

    private boolean newMin(int x, int y) {
        return (x < y || y == -1);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Bag<Integer> bv = new Bag<Integer>();
        bv.add(v);
        Bag<Integer> bw = new Bag<Integer>();
        bw.add(w);
        return ancestor(bv, bw);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
        int length = -1;
        int ancestor = -1;

        ArrayList<Integer> ancestors = findAncestors(bfsv);
        ancestors.retainAll(findAncestors(bfsw));

        for (int a: ancestors) {
            int d = bfsv.distTo(a) + bfsw.distTo(a);
            if (newMin(d, length)) {
                length = d;
                ancestor = a;
            }
        }

        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        System.out.println("testLength: " + testLength());
        System.out.println("testAncestor: " + testAncestor());
        System.out.println("testImmutable: " + testImmutable());
        System.out.println("testIterable: " + testIterable());
        System.out.println("testLengthTime: " + testLengthTime());
    }

    private static boolean testLength() {
        In in = new In("wordnet/digraph1.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        return sap.length(3, 3) == 0;
    }

    private static boolean testAncestor() {
        In in = new In("wordnet/digraph1.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        return sap.ancestor(3, 3) == 3;
    }

    private static boolean testImmutable() {
        In in = new In("wordnet/digraph1.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        int before = sap.length(0, 3);
        G.addEdge(0,3);
        int after = sap.length(0, 3);
        return before == after;
    }

    private static boolean testIterable() {
        In in = new In("wordnet/digraph-wordnet.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        Random r = new Random();
        Bag<Integer> v = new Bag<Integer>();
        Bag<Integer> w = new Bag<Integer>();

        for(int i = 0; i < 3; i++) {
            v.add(r.nextInt(G.V()));
        }
        for(int i = 0; i < 11; i++) {
            w.add(r.nextInt(G.V()));
        }
        sap.length(v, w);
        sap.ancestor(r.nextInt(G.V()), r.nextInt(G.V()));
        return loopCount < 1000; // research - how to test the number of primitive operations in G - maybe a jUnit capability
    }

    private static boolean testLengthTime() {
        // time ancestor() with random subsets of 5 vertices
        In in = new In("wordnet/digraph-wordnet.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        Random r = new Random();
        Bag<Integer> v = new Bag<Integer>();
        Bag<Integer> w = new Bag<Integer>();
        long start = System.nanoTime();
        int count = 0;
        do {
            for (int j = 0; j < 5; j++) {
                v.add(r.nextInt(G.V()));
            }
            for(int j = 0; j < 5; j++) {
                w.add(r.nextInt(G.V()));
            }
            sap.length(v, w);
            count++;
        } while (System.nanoTime() - start < 1000000000);
        return count > 1000;
    }

    private static boolean testAncestorTime() {
        // time length() with random subsets of 5 vertices
        In in = new In("wordnet/digraph-wordnet.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        Random r = new Random();
        Bag<Integer> v = new Bag<Integer>();
        Bag<Integer> w = new Bag<Integer>();
        long start = System.nanoTime();
        int count = 0;
        do {
            for (int j = 0; j < 5; j++) {
                v.add(r.nextInt(G.V()));
            }
            for(int j = 0; j < 5; j++) {
                w.add(r.nextInt(G.V()));
            }
            sap.ancestor(v, w);
            count++;
        } while (System.nanoTime() - start < 1000000000);
        return count > 1000;
    }

}
