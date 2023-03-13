//Fatima Khalid
//fxk200007
//Generic Node class
public class Node <Type extends Comparable<Type>> implements Comparable<Node<Type>>{
    private Type obj; //holds object
    private Node<Type> left;
    private Node<Type>  right;
    
    //constructor
     public Node(Type item) {obj = item;}
     
    //accessors
    public Type getObj(){return obj;}
    public Node getLeft(){return left;}
    public Node getRight(){return right;}
    //mutators
    public void setObj(Type item) {obj = item;}
    public void setLeft(Node newLeft) { left = newLeft;}
    public void setRight(Node newRight) { right = newRight;}
    
    //compare two nodes
    @Override
    public int compareTo(Node<Type> anotherNode){return getObj().compareTo(anotherNode.getObj());}
}