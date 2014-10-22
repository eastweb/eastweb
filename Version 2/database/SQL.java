package edu.sdstate.eastweb.prototype.database;

public class SQL {
    final String select = "SELECT";
    final String from = "FROM";
    final String where = "WHERE";
    final String end = ";";

    private String table;
    private String[] field;
    private String[] whstat;
    private String query;

    public SQL(String[] field, String project){
        this.field = field.clone();
        table = "\""+DatabaseManagerOld.getSchemaName(project) + "\"" + ".\"ZonalStats\"";
        query = select + "\n";
        // i starts with 1 because first string is space
        for(int i = 1; i < this.field.length ; i++){
            query = query + "\t" + this.field[i] +",\n";
        }
        query = query.substring(0, query.length()-2) + "\n";
        query = query + from +"\n"
                + "\t" + table + ";\n";
    }

    public SQL(String[] field, String project, String[] where){
        this.field = field.clone();
        table = "\""+DatabaseManagerOld.getSchemaName(project) + "\"" + ".\"ZonalStats\"";
        whstat = where.clone();

        query = select + "\n";
        // i starts with 1 because first string is space
        for(int i = 1; i < this.field.length ; i++){
            query = query + "\t" + this.field[i] +",\n";
        }
        query = query.substring(0, query.length()-2) + "\n";
        query = query + from +"\n"
                + "\t" + table + "\n"
                + " INNER JOIN \"" + DatabaseManagerOld.getSchemaName(project) + "\".\"Zones\""
                + " ON \"" + DatabaseManagerOld.getSchemaName(project) + "\".\"ZonalStats\".\"zoneID\"=\"" + DatabaseManagerOld.getSchemaName(project) + "\".\"Zones\".\"zoneID\"\n"
                + this.where + "\n";
        // i starts with 1 because first string is space
        for(int i = 1; i < whstat.length ; i++){
            query = query + "\t"+whstat[i]+" AND\n";
        }

        query = query.substring(0, query.length()-5) + end;

        System.out.println(query);
    }

    public void printQuery(){
        System.out.println(query);
    }

    public String getQuery(){
        return query;
    }
}
