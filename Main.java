//Fatima Khalid
//fxk200007
import java.util.*;
import java.io.*;
import java.lang.*;
public class Main
{
	public static void main(String[] args) throws IOException
    {
	    String fileName; //to hold file name
        Scanner consoleIn = new Scanner(System.in); //to get user input
        fileName = consoleIn.next(); //set file name to what user enters
        
        File inFile = new File(fileName); //to open file
        Scanner in = new Scanner(inFile); //to read from file
        
        
        String line; //to hold one line from file at a time
        if (inFile.canRead()) //if file does not open properly
        {
            while (in.hasNext()) //while the file still has data in it
            {
                BinTree<Term> tree = new BinTree<>(); //create an empty tree to hold expression
                line = in.nextLine(); //store the data for one expression 
                //to use if the integral must be solved
                boolean isDefinite = false; 
                int upperBound = 0;
                int lowerBound = 0;
                if(line.charAt(0) != '|') //if the integral has bounds (is definite)
                {
                    isDefinite = true;
                    //extract and store bounds from expression
                    upperBound = Integer.parseInt(line.substring(0, line.indexOf('|')));
                    lowerBound = Integer.parseInt(line.substring(line.indexOf('|')+1, line.indexOf(' ')));
                    //update expression to a common indefinite integral format
                    line = line.substring(line.indexOf(' ')+1);
                    line = '|' + line;
                }
                line = RemoveSpaces(line); //remove spaces from line
                String[] terms = SplitExpression(line); //store all terms in an array
                for(int i = 0; i < terms.length; i++) //for every term
	            {
	                Term t = Antiderivative(terms[i]); //Create a term holding the antiderivative of the term
	                Node<Term> n = new Node<>(t); //Store term in a new node
	                //check for duplicates before inserting
	                if(tree.Search(n)!=null) //if there is a like term existing
	                {
	                    Node<Term> existing = tree.Search(n); //find the existing term
	                    addCoeffs(existing, n); //update the coefficient of existing term
	                }
	                else //no like term existing
	                {
	                    tree.Insert(n); //insert node into tree
	                }
	            }
	            Print(tree.getRoot(), findMaxExp(tree.getRoot())); //print tree 
	            
	            if(isDefinite) //if integral must be solved
	            {
	                double upperSum = SumTree(tree.getRoot(), upperBound); //calculate sum of expression from plugging in upper bound
	                double lowerSum = SumTree(tree.getRoot(), lowerBound);//calculate sum of expression from plugging in lower bound
	                double integralVal = upperSum-lowerSum; //find value of integral
	                
	                if(upperBound < lowerBound) //if upper bound is less than lower bound 
	                {
	                    integralVal = integralVal * -1; //flip sign of integral
	                }
	                //print bounds and answer
	                System.out.printf(", %d|%d = %.3f", upperBound, lowerBound, integralVal); 
	                System.out.println();
	            }
	            else //indefinite integral
	            {
	                System.out.println(" + C"); //print constant
	            }
            }
        }
        in.close(); //close input file
    }
    
	public static String RemoveSpaces(String s) //isolate expression with no whitespace
	{
	    s = s.replaceAll("\\s", ""); //remove spaces
	    s = s.substring (0, s.length()-2); //chop off dx
	    return(s.substring(1)); //chop off pipe character
	}
	public static String[] SplitExpression(String line) //split expression string into term strings
	{
	    //chop off first negative
	    boolean modifiedBeginning = false;
	    if(line.charAt(0) == '-')
	    {
	        line = line.substring(1);
	        modifiedBeginning = true;
	    }
	        //replace all - with +- to make splitting possible
	        line = line.replaceAll("-","+-"); //+3x^3+-x^2+x+-4"
	        //go through string, looking for any ^+ to make sure no negative exponents are split from their terms
    	    while(line.indexOf("^+") != -1)
    	    {
    	        //get rid of extra plus signs 
    	        String s1 = line.substring(0, line.indexOf("^+")+1);
    	        String s2 = line.substring(line.indexOf("^+")+2);
    	        line = s1.concat(s2);
    	    }
            String[] allTerms = line.split("\\+"); //split expression into terms { 3x^3, -x^2, x, -4 }
            if(modifiedBeginning) //if first coefficient was modified 
            {
                allTerms[0] = '-'+allTerms[0]; //add the negative sign back
            }
            return allTerms;//return array 

	}
	public static Term Antiderivative(String term)
	{
	    Term t = new Term(); //create empty term
	    
	    //CONSTANT TERM: -3, 4
	    if(term.indexOf('x') == -1)
	    {
	        //store its antiderivative (-3x)
	        t.setCoeff(Integer.parseInt(term));
	        t.setExp(1);
	    }
	    //ONE COEFFICIENT x^2, -x
	    else if((term.charAt(0) == 'x') || (term.charAt(0)=='-' && term.charAt(1) == 'x')) 
	    {
	        if(term.charAt(0)=='-' && term.charAt(1) == 'x')//NEGATIVE ONE COEFF
	        {
	            t.setCoeff(-1);
	        }
	        
	        if(term.indexOf('^')!= -1) //HAS EXPONENT
	        {
	            t.setExp(Integer.parseInt(term.substring(term.indexOf('^')+1)));
	        }
	        //exponents are set with current values,
	           //update exponents with antiderivatives 
	        t.setExp(t.getExp()+1);
	        t.setDenom(t.getExp());
	    }
	    //NUMERICAL COEFFICIENT 3x^-2, -12x^-10, 23x
	    else
	    {
	        t.setCoeff(Integer.parseInt(term.substring(0, term.indexOf('x')))); //store coefficient 
	        if(term.indexOf('^')!= -1) //if exponent 
	        {
	            t.setExp(Integer.parseInt(term.substring(term.indexOf('^')+1))); //store exponent
	        }
	        //update coefficients and exponents to antiderivatives
	        t.setExp(t.getExp()+1);
	        t.setDenom(t.getExp());
	    }
	    return t; //return term
	}
	public static void Print(Node n2, int maxExp) //print expression
    {
        Node<Term> n = n2; 
      
      if(n==null) //if node is null, nothing to print
          return;  
      
      //Print node on right of n
      Print(n.getRight(), maxExp);
      
      //simplify fraction
      simplifyFrac(n, (n.getObj()).getCoeff(), (n.getObj()).getDenom()); //pass in node, object's numerator, and object's denominator

      if(n.getObj().getExp() == maxExp) //FIRST TERM
      {
         //PRINT COEFFICIENTS
        if( (double)(n.getObj().getCoeff()) / (double)(n.getObj().getDenom()) == 1.0) //1/1 or -1/-1 POSITIVE ONE COEFFICIENT 
        {
        }
        else if( (double)(n.getObj().getCoeff()) / (double)(n.getObj().getDenom()) == -1.0) //1/-1NEGATIVE ONE COEFFICIENT 
        {
            
            System.out.print("-");
        }
        else if(n.getObj().getDenom() == 1 || n.getObj().getDenom() == -1) //(2/1 or 2/-1 or 1/-1) WHOLE NUMBER
        {

            if(n.getObj().getCoeff() < 0 || n.getObj().getDenom() < 0) //if it's a negative whole number
            {
                System.out.print("-" + Math.abs(n.getObj().getCoeff()));
            }
            else //it's a positive whole number
            {
                System.out.print(Math.abs(n.getObj().getCoeff()));
            }
        }
        else //FRACTIONAL COEFFICIENT 
        {
            if(n.getObj().getDenom() < 0 )//if denom is negative, turn it positive and make numerator negative
            {
                n.getObj().setDenom( (n.getObj().getDenom()*-1) ); //turn denom positive
                n.getObj().setCoeff( (n.getObj().getCoeff()*-1) ); //change sign of numerator
            }
            if(n.getObj().getCoeff() < 0 ) //negative fraction
            {
                System.out.print("(-"  + (n.getObj().getCoeff()*-1) + "/" + n.getObj().getDenom() + ")");
            }
            else //positive fraction
            {
                System.out.print("("  + n.getObj().getCoeff() + "/" + n.getObj().getDenom() + ")");
            }
        }
      }
      
      else //NOT FIRST TERM
      {
        //PRINT COEFFICIENTS
        if( (double)(n.getObj().getCoeff()) / (double)(n.getObj().getDenom()) == 1.0) //1/1 or -1/-1 POSITIVE ONE COEFFICIENT 
        {
            System.out.print(" + ");
        }
        else if( (double)(n.getObj().getCoeff()) / (double)(n.getObj().getDenom()) == -1.0) //1/-1NEGATIVE ONE COEFFICIENT 
        {
            System.out.print(" - ");
        }
        else if(n.getObj().getDenom() == 1 || n.getObj().getDenom() == -1) //(2/1 or 2/-1 or 1/-1) WHOLE NUMBER
        {
            if(n.getObj().getCoeff() < 0 || n.getObj().getDenom() < 0) //if it's a negative whole number
            {
                System.out.print(" - " + Math.abs(n.getObj().getCoeff()));
            }
            else
            {
                System.out.print(" + " + Math.abs(n.getObj().getCoeff()));
            }
        }
        else //FRACTIONAL COEFFICIENT
        {
            if(n.getObj().getDenom() < 0 )//if denom is negative, turn it positive and change sign of numerator 
            {
                n.getObj().setDenom( (n.getObj().getDenom()*-1) ); //turn denom positive
                n.getObj().setCoeff( (n.getObj().getCoeff()*-1) ); //change sign of numerator
            }
            if(n.getObj().getCoeff() < 0 ) //negative fraction
            {
                System.out.print(" - ("  + (n.getObj().getCoeff()*-1) + "/" + n.getObj().getDenom() + ")");
            }
            else //positive fraction
            {
                System.out.print(" + ("  + n.getObj().getCoeff() + "/" + n.getObj().getDenom() + ")");
            }
        }
      }
        //PRINT REST OF TERM
        if(n.getObj().getCoeff() != 0) //if nonzero coefficient
        {
            System.out.print("x");
            //PRINT EXPONENTS
            if(n.getObj().getExp() != 1) //if exponent is not 1 
            {
                System.out.print("^" + n.getObj().getExp()); //print exponent
            } 
        }
      Print(n.getLeft(), maxExp); //print left side
    }
    public static void simplifyFrac(Node n2, int num, int denom)
    {
        Node<Term> n = n2;
        //find greatest common denominator
        int gcd; 
        gcd = findGCD(num, denom);
        //divide numerator and denom by gcd
        num = num / gcd;
        denom = denom / gcd;
        //update numerator and denominator of terms
        n.getObj().setCoeff(num);
        n.getObj().setDenom(denom);
    }
    public static int findGCD(int num, int denom) //calculate greatest common denominator 
    {
        if (denom == 0)
            return num;
        return findGCD(denom, (num%denom));
    }
    public static int findMaxExp(Node n2) //find maximum exponent in antiderivative expression
    {
        Node<Term> n = n2; 
        if (n == null) //if no value, return -infinity
            return Integer.MIN_VALUE;
        
        //find maximum of left and right sides of tree
        int max = (n.getObj()).getExp();
        int maxL = findMaxExp(n.getLeft());
        int maxR = findMaxExp(n.getRight());
        //find true maximum of all subtrees
        if (maxL > max)
            max = maxL;
        if (maxR > max)
            max = maxR;
        return max;
    }
    public static void addCoeffs(Node original1, Node add1) //update existing node's coefficient with like term
    {
        Node<Term> original = original1;
        Node<Term> add = add1;
        int newDenom = findGCD(original.getObj().getDenom(), add.getObj().getDenom()); //find GCD of both node's denominators
        newDenom = (original.getObj().getDenom()*add.getObj().getDenom()) / newDenom; //find least common multiple of both denominators
     
        //solve for new numerator (add both after cross multiplying)
        int newNum = (original.getObj().getCoeff())*(newDenom/original.getObj().getDenom()) + 
                        (add.getObj().getCoeff())*(newDenom/add.getObj().getDenom());
        simplifyFrac(original, newNum, newDenom); //make sure fraction is simplified
    }
    public static double SumTree(Node n2, int plugIn) //add up terms if definite integral
    {  
        double sum, sumL, sumR;  
        sum = sumR = sumL = 0;  
        Node<Term> n = n2;
        //Check whether tree is empty  
        if(n == null) 
        {  
            return 0;  
        }  
        else 
        {  
            //Calculate the sum of nodes on left side of tree  
            if(n.getLeft() != null)  
                sumL = SumTree(n.getLeft(), plugIn);  
            //Calculate the sum of nodes on right side 
            if(n.getRight() != null)  
                sumR = SumTree(n.getRight(), plugIn);  
            //find sum of all nodes
            sum = calcNodeValue(n, plugIn) + sumL + sumR;
            return sum;  
        }  
    } 
    public static double calcNodeValue(Node n2, int plugIn) //calculate value of an individual node if an x is given
    {
        Node<Term> n = n2;
        
        double val = Math.pow( (double)plugIn , (double)(n.getObj().getExp()) );//raise x to the power of its exponent
        val = val * (n.getObj().getCoeff());//multiply by numerator
        val = val/(n.getObj().getDenom());//divide by denominator
        return val;
    }
}
