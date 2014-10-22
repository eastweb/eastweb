package edu.sdstate.eastweb.prototype.reprojection;

import java.util.ArrayList;

/**
 * Used to generate ArcGIS projection strings.
 * 
 * @author isaiah
 */
public class ProjNode {

    private String name;
    private ArrayList<Object> elements;

    public ProjNode(String name) {
        this.name = name.toUpperCase();
        elements = new ArrayList<Object>();
    }

    public void add(String element) {
        elements.add("\"" + element + "\"");
    }

    public void add(ProjNode element) {
        elements.add(element);
    }

    public void add(double element) {
        elements.add(new Double(element));
    }

    public void add(int element) {
        elements.add(new Integer(element));
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int i;
        for (i=0; i<elements.size()-1; i++) {
            string.append(elements.get(i).toString());
            string.append(',');
        }
        string.append(elements.get(i++));

        return name + "[" + string + "]";
    }

}
