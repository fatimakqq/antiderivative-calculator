//Fatima Khalid
//fxk200007
public class Term implements Comparable<Term>
{
    private int coeff;
    private int denom;
    private int exp;
    //constructors
    public Term(){coeff = 1; denom = 1; exp = 1;} //default term is an x
    public Term(int c, int e) {coeff = c; exp = e;} //overloaded
    //accessors
    public int getCoeff(){return coeff;}
    public int getExp(){return exp;}
    public int getDenom(){return denom;}
    //mutators
    public void setCoeff(int c){coeff = c;}
    public void setExp(int e){exp = e;}
    public void setDenom(int d){denom = d;}
    //compare two terms by exponent
    @Override
    public int compareTo(Term o) {return this.getExp() - o.getExp();}
}