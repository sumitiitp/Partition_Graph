import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;


public class Efficient_Partition_Graph {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("The program to generate the partition graph starts...");
        Generate_Graph gg = new Generate_Graph();
        int n = 10; // Give integer input
        gg.generateGraph(10);
        System.out.println("The program to generate the partition graph ends...");
    }
}



class Generate_Graph {
    public void generateGraph (int n) {
        // Calculate the number of partitions using Hardy-Ramanujan Asymptotic Partition Formula
        double pn_numerator = Math.PI * Math.sqrt((double)2*n/(double)3);
        double pn_denominator = 4*n* Math.sqrt(3);
        int pn = (int) Math.ceil(Math.exp(pn_numerator) / pn_denominator);
        
        int noVertex = 0;
        int noEdges = 0;
        
        /* Adjancy list representation of the graph (Considered the number of partitions given by 
         * Hardy-Ramanujan Asymptotic Partition Formula) */
        LinkedList<Integer>[] adjList = new LinkedList[pn];
        /* An array of Vertex so that the partition can be accessed in constant time given the id 
         * for that corresponding partition */
        Vertex[] toAccessPartition = new Vertex[pn];
        for(int i = 0; i < pn; i++) {
            toAccessPartition[i] = new Vertex();
            adjList[i] = new LinkedList();
        }
        
        /* First node of the graph */
        int id = 0;
        int[] elements = {n};
        Vertex vertex = new Vertex(id, elements);
        
        toAccessPartition[id] = new Vertex(vertex);
        noVertex++;
        
        Queue<Integer> Q = new LinkedList();
        Q.add(id);

        Trie trie = new Trie();
        int lengthToChcekTrie = 0;
        while(!Q.isEmpty()) {
            Vertex curVertex = new Vertex();
            int currPartitionId = Q.poll();
            curVertex = toAccessPartition[currPartitionId];
            int len = curVertex.getElements().length;
            
            if(lengthToChcekTrie != len) {
                trie = new Trie();
            }
            
            if(len == n) { // All the partition have been generated
                break;
            }
            
            /* Generate all the partition of length len+1 from a partition curVertex.getElements() of 
             * length len */
            for(int i = 0; i < len; i++) {
                if ((len==1) || i == 0 || (curVertex.getElements()[i] != curVertex.getElements()[i-1]) ) {
                    int[] newPartition = new int[len + 1];
                    if(i-1 >= 0) {
                        System.arraycopy(curVertex.getElements(), 0, newPartition, 0, i-1+1);
                    }
                    System.arraycopy(curVertex.getElements(), i+1, newPartition, i, len-(i+1));

                    for(int j = 1; j <= curVertex.getElements()[i]/2; j++) {
                        newPartition[newPartition.length-2] = j;
                        newPartition[newPartition.length-1] = curVertex.getElements()[i]-j;
                        int[] sortedNewPartition = new int[newPartition.length];
                        sortedNewPartition = countingSort(newPartition);
                        
                        /* Chcek whether the generated partition has been alreday generated or not
                         using m-ary tree based data structure */
                        SearchNode searchNode = new SearchNode();
                        if(trie.root == null) {
                            searchNode = new SearchNode(false, -1);
                        } else {
                            searchNode = trie.search(sortedNewPartition);
                        }
                        
                        noEdges++;
                        if(searchNode.isIsPresent()) {
                           int existingNodeid = searchNode.getId();
                           adjList[currPartitionId].add(existingNodeid);   
                        } else {
                            id++;   
                            vertex = new Vertex(id, sortedNewPartition);
                            adjList[currPartitionId].add(id);   
                            toAccessPartition[id] = new Vertex(vertex);
                            trie.insert(sortedNewPartition, n, id);
                            noVertex++;
                            Q.add(id);
                        }
                    }
                }
            }  
            lengthToChcekTrie = len;
        }
        System.out.println("nodes = " + noVertex);
        System.out.println("edges = " + noEdges);
    }

    
    public int[] countingSort(int[] array) { 
        int[] aux = new int[array.length];

        // find the smallest and the largest value
        int min = array[0];
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            } else if (array[i] > max) {
                max = array[i];
            }
        }

        // init array of frequencies
        int[] counts = new int[max - min + 1];

        // init the frequencies
        for (int i = 0;  i < array.length; i++) {
          counts[array[i] - min]++;
        }

        // recalculate the array - create the array of occurences
        counts[0]--;
        for (int i = 1; i < counts.length; i++) {
          counts[i] = counts[i] + counts[i-1];
        }

        for (int i = array.length - 1; i >= 0; i--) {
            aux[counts[array[i] - min]--] = array[i];
        }
        
        int[] auxDescending = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            auxDescending[i] = aux[array.length - i - 1];
        }
        return auxDescending;
    } 
}


class Vertex {
    private int vertexId;
    private int elements[];
    
    public Vertex() {
    }

    public Vertex(int nodeId, int[] elements) {
        this.vertexId = nodeId;
        this.elements = elements.clone();
    }

    public Vertex(Vertex vertex) {
        this.vertexId = vertex.vertexId;
        this.elements = vertex.elements.clone();
    }
    
    public int getVertexId() {
        return vertexId;
    }

    public int[] getElements() {
        return elements;
    }

    public void setVertexId(int vertexId) {
        this.vertexId = vertexId;
    }

    public void setElements(int[] elements) {
        this.elements = elements.clone();
    }

    @Override
    public String toString() {
        return "Vertex{" + "vertexId=" + vertexId + ", elements=" + Arrays.toString(elements) + '}';
    }
}



class TrieNode {
    int key; 
    TrieNode[] children; 
    boolean isLeaf;
    int Id;
  
    public TrieNode() { 
        this.isLeaf = false;
    } 

    public TrieNode(int key) { 
        this.key = key; 
        this.isLeaf = false;
    }   
    
    public TrieNode(int key, int size) { 
        this.key = key; 
        this.children = new TrieNode[size];
        this.isLeaf = false;
        this.Id = -1;
    } 

    public TrieNode(int key, int size, boolean isleaf, int id) { 
        this.key = key; 
        this.children = new TrieNode[size];
        this.isLeaf = true;
        this.Id = id;
    } 
    
    @Override
    public String toString() {
        return "TrieNode{" + "key=" + key + ", children=" + children + ", isLeaf=" + isLeaf + ", Id=" + Id + '}';
    }  
}


class Trie {
    // Root of BST 
    TrieNode root; 
  
    // Constructor 
    Trie() {  
        this.root = null; 
    } 
    
    /* Insert partition P whose id is 'id' into the m-ary tree */
    void insert(int[] P, int n, int id) {
        int level; 
        int k = P.length; 
        int index; 
        int maxNoChild = n;
       
        if(root == null) {
            root = new TrieNode(n, maxNoChild);    
        } else {
            //do nothing
        }
        TrieNode trieNode = root; 

        for (level = 0; level < k-1; level++)  {
            index = P[level];
            if (trieNode.children[index] == null) {
                trieNode.children[index] = new TrieNode(P[level],maxNoChild);
            }
            trieNode = trieNode.children[index]; 
        }
        index = P[level];
        trieNode.children[index] = new TrieNode(index,maxNoChild, true, id);
    } 

    
    /* Search a partition P into the m-ary tree */
    SearchNode search(int[] P) {
        int level; 
        int length = P.length; 
        int index; 
        TrieNode trieNode = root; 
       
        for (level = 0; level < length; level++) {
            index = P[level]; 
            if (trieNode.children[index] == null) {
                SearchNode searchNode = new SearchNode(false, -1);
                return searchNode; 
            }
            trieNode = trieNode.children[index]; 
        } 
       
        if (trieNode == null) {
            SearchNode searchNode = new SearchNode(false, -1);
            return searchNode; 
        } else {
            SearchNode searchNode = new SearchNode(true, trieNode.Id);
            return searchNode; 
        }
    }  
}



class SearchNode {
    private boolean isPresent;
    private int id;

    public SearchNode() {
    }

    public SearchNode(boolean isPresent, int id) {
        this.isPresent = isPresent;
        this.id = id;
    }

    public boolean isIsPresent() {
        return isPresent;
    }

    public int getId() {
        return id;
    }

    public void setIsPresent(boolean isPresent) {
        this.isPresent = isPresent;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SearchNode{" + "isPresent=" + isPresent + ", id=" + id + '}';
    }
}
