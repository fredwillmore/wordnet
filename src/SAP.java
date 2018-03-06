import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;

import java.util.ArrayList;

public class SAP {
    // constructor takes a digraph (not necessarily a DAG)

    private Digraph G;
    public SAP(Digraph G) {
        if (G == null)
            throw new java.lang.IllegalArgumentException();
        this.G = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, w);
        int sapLength = -1;

        ArrayList<Integer> ancestors = findAncestors(bfsv);
        ancestors.retainAll(findAncestors(bfsw));

        for (int a: ancestors) {
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
            if(bfs.hasPathTo(v))
                a.add(v);
        }
        return a;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(G, G.adj(v));
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(G, G.adj(w));
        int length = -1;
        int ancestor = -1;

        ArrayList<Integer> ancestors = findAncestors(bfsv);
        ancestors.retainAll(findAncestors(bfsw));

        for (int a: ancestors) {
            int d = bfsv.distTo(a) + bfsw.distTo(a);
            if (d < length || length == -1) {
                length = d;
                ancestor = a;
            }
        }

        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int length = -1;

        for(int vi: v) {
            for(int wi: w) {
                int d = length(vi, wi);
                if (d < length || length == -1) {
                    length = d;
                }
            }
        }

        return length;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int ancestor = -1;
        int length = -1;

        if (v == null || w == null)
            throw new java.lang.IllegalArgumentException();

        for(int vi: v) {
            for(int wi: w) {
                int d = length(vi, wi);
                if (d < length || length == -1) {
                    length = d;
                    ancestor = ancestor(wi, vi);
                }
            }
        }

        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}
