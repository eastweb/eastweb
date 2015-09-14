package edu.sdstate.eastweb.prototype.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Arrays;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;

public class Database {
    Connection conn;
    Statement stmt;
    ResultSet rs;

    final String url;
    private String id;
    private String pass;
    private String query;
    private String[] fields;
    Config config;

    public static void main(String[] args) throws ConfigReadException, SQLException {
        args[0] = "SELECT year, day, \"zoneID\", count, sum, mean, stdev, index FROM \"project_eth_utm\".\"ZonalStats\" WHERE (\"zoneID\" >= 0) AND (year >= 0) AND (day >= 0) AND (index IN (0,1,2,3,4,5,6,7,8,9,10));";
        Database db = new Database(args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    public Database (String query, String[] fields) throws SQLException, ConfigReadException{
        config = Config.getInstance();
        conn = null;
        stmt = null;
        rs = null;
        url = config.getDatabaseHost();
        id = config.getDatabaseUsername();
        pass = config.getDatabasePassword();
        this.query = query;
        this.fields = fields.clone();

        excution();
    }

    public void excution() throws SQLException{

        // Driver Connection Check
        try{
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException e){
            System.out.println("Exception :: JDBC Driver Not Found"); // FIXME: should be exception
        }

        try{
            System.out.println("TRACE: getting DB connection.");
            //SQL Execution
            conn = DriverManager.getConnection(url, id, pass);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.setFetchSize(1000); // Prevent too much memory from being used
            rs = stmt.executeQuery(query);


            System.out.println("TRACE: writing temporary CSV file.");
            File tmpFile = new File("./temp.csv");
            PrintWriter tmp = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(tmpFile)));

            // write fields at the first row in the temporary file
            for(int i = 1; i<fields.length ; i++){
                tmp.print(fields[i]);
                if(i+1<fields.length) {
                    tmp.print(",");
                }
            }
            tmp.println();

            // Get the result from the database and save the result as a temporary file
            while(rs.next()){
                for(int i = 1; i<fields.length ; i++){
                    tmp.print(rs.getString(i));
                    if(i+1<fields.length)
                    {
                        tmp.print(",");
                        //System.out.print(rs.getString(i)+",");
                    }
                }
                tmp.println();
                //System.out.println();
            }

            rs.close();
            stmt.close();
            conn.close();
            tmp.close();

            System.out.println("TRACE: done writing temporary CSV file.");

        }catch(IOException e){
            System.err.println("Execution failure: "+e);
            System.exit(1);
        }
    }

    void setId(String id){
        this.id = id;
    }

    void setPass(String pass){
        this.pass = pass;
    }

    void setQuery(String query){
        this.query = query;
    }

    String getUrl(){
        return url;
    }

    String getID(){
        return id;
    }

    String getPass(){
        return pass;
    }

    String getQuery(){
        return query;
    }
}
