//Fatima Khalid
//fxk200007
//Generic Binary Tree class
public class BinTree<Type extends Comparable<Type>> {
    
    private Node<Type> root;
    //constructors
    public BinTree(){};
    //accessors
    public Node getRoot(){return root;}
    //mutators
    public void setRoot(Node newRoot){root = newRoot;}
    
    //INSERT
    public void Insert(Node n) 
    {
       if (root==null) //if no nodes yet
       {
          root = n;
       }
       else
          InsertRecursive(root, n); //recursively insert node
    }
    public void InsertRecursive(Node par, Node insNode) 
    {
        if (insNode.compareTo(par) < 0)  //insert node is less than parent
       { 
          if (par.getLeft() == null) //if the parent doesn't have a left child
             par.setLeft(insNode);//make node to insert its left child
          else
             InsertRecursive(par.getLeft(), insNode); //continue down left subtree
       }
       else //insert node is greater than parent
       {
          if (par.getRight() == null) //if the parent doesn't have a right child
             par.setRight(insNode); //make node to insert its right child
          else
             InsertRecursive(par.getRight(), insNode); //continue down right subtree
       }
    }
        
    //SEARCH
    public Node Search(Node keyNode) {return SearchRecursive(root, keyNode);} 
    public Node SearchRecursive(Node n, Node keyNode) 
    {
        if (n!=null) //if node exists
        {
          if (keyNode.compareTo(n)==0) //node found!
             return n;
          else if (keyNode.compareTo(n) < 0) //node is greater than node we're looking for
             return SearchRecursive(n.getLeft(), keyNode); //go down left subtree
          else //node < node we're looking for 
             return SearchRecursive(n.getRight(), keyNode); //go down right subtree
        }
        return null;
    }
    //GET PARENT (helps for remove function)
    public Node GetParent(Node n) {return GetParentRecursive(root, n);}
    public Node GetParentRecursive(Node subRoot, Node n) 
    {
       if (subRoot == null) //if no node
          return null;
    
       if (subRoot.getLeft() == n || subRoot.getRight() == n)  //if node is found, return its parent
       {
          return subRoot;
       }
       if (n.compareTo(subRoot)<0)  //if node is less than the subtree's root
       {
          return GetParentRecursive(subRoot.getLeft(), n); //go down left subtree
       }
       return GetParentRecursive(subRoot.getRight(), n); //go down right subtree
    }
    
    //REMOVE
    public void Remove(Node keyNode) 
    {
       Node n = Search(keyNode); //locate node to remove
       Node parent = GetParent(n); //locate its parent
       RemoveNodeRecursive(parent, n); //remove node recursively
    }
    public boolean RemoveNodeRecursive(Node parent, Node n) {
       if (n == null) //if node doesn't exist
          return false;
       //INTERNAL NODE W 2 CHILDREN
       if (n.getLeft() != null && n.getRight() != null) 
       {
          //Find node after n and its parent
          Node successorNode = n.getRight();
          Node successorParent = n;
          //while the successor has left children
          while (successorNode.getLeft() != null) 
          {
              //move successor and its parent down tree
             successorParent = successorNode;
             successorNode = successorNode.getLeft();
          }
          n.setObj(successorNode.getObj()); //copy over successor's data
          RemoveNodeRecursive(successorParent, successorNode); //remove successor
       }
    
       //ROOT NODE W 0,1 CHILDREN
       else if (n == root)
       {
          if (n.getLeft() != null) //if root has left children
             root = n.getLeft(); //move root down left subtree
          else
             root = n.getRight(); //move root down right subtree
       }
       //INTERNAL NODE W ONLY LEFT CHILD
       else if (n.getLeft() != null)
       {
          if (parent.getLeft() == n) //if parent's left child is node
             parent.setLeft(n.getLeft()); //set parent's left to node's left
          else
             parent.setRight(n.getLeft()); //set parents's left to node's right
       }
    
       //LEAF OR INTERNAL NODE W ONLY RIGHT CHILD
       else 
       {
          // Replace node with node's right child
          if (parent.getLeft() == n) //if parent's left child is node
             parent.setLeft(n.getRight());//set parent's left to node's left
          else
             parent.setRight(n.getRight());//set parents's right to node's right
       }        
       return true; //node removed successfully
    }
}