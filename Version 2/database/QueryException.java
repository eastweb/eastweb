package edu.sdstate.eastweb.prototype.database;

public class QueryException extends Exception{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryException(){
        super("There is no record.");
    }
}
