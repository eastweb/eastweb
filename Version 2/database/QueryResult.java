package edu.sdstate.eastweb.prototype.database;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import edu.sdstate.eastweb.prototype.ConfigReadException;

import java.io.*;
import java.sql.SQLException;

public class QueryResult extends Dialog {

    private static final int MAX_DISPLAYED_RESULTS = 10000;

    Shell shell;
    Table result;
    String query;
    String csvFile;
    Database exe;

    public QueryResult(Shell parent, String[] titles, String query) throws SQLException, ConfigReadException {
        super(parent);
        this.query = query;

        exe = new Database(this.query, titles);
    }

    public void open() {
        shell = new Shell(getParent(), SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setMinimumSize(300, 200);

        shell.setText("Query Result");
        {
            GridLayout layout = new GridLayout(2, false);
            shell.setLayout(layout);
        }

        setupWidgets();

        shell.setLocation(
                getParent().getLocation().x + (getParent().getSize().x - shell.getSize().x)/2,
                getParent().getLocation().y + (getParent().getSize().y - shell.getSize().y)/2
                );
        shell.open();
        Display display = shell.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    public void setupWidgets() {
        try{
            System.out.println("TRACE: reading from temporary CSV.");

            File tmpFile = new File("./temp.csv");
            BufferedReader tmp = new BufferedReader(new FileReader(tmpFile));

            new Label(shell, SWT.NONE).setText("Results of specified SQL query.");

            // Composite for Main
            result = new Table(shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
            result.setLinesVisible(true);
            result.setHeaderVisible(true);
            result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

            String[] titles = tmp.readLine().split(",");
            for(int i = 0; i < titles.length; i++){
                TableColumn column = new TableColumn(result, SWT.NONE);
                column.setText(titles[i]);
            }

            String temp;
            int count = 0;
            while((temp = tmp.readLine())!=null) {
                if (count < MAX_DISPLAYED_RESULTS) {
                    String[] records = temp.split(",");
                    TableItem item = new TableItem(result, SWT.NONE);
                    for(int j = 0; j < titles.length; j++){
                        item.setText(j, records[j]);
                    }
                }
                count++;
            }
            if (count >= MAX_DISPLAYED_RESULTS) {
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                messageBox.setText("Too many results");
                messageBox.setMessage("Could only display " + MAX_DISPLAYED_RESULTS + " of " + count + " results. Click the \"Save to file\" button and open with 3rd party tool to view all results."); // FIXME: should dynamically load results instead.
                messageBox.open();
            }
            for(int i=0; i<titles.length; i++){
                result.getColumn(i).pack();
            }
            tmp.close();

            System.out.println("TRACE: done reading from temporary CSV.");
        }catch(IOException e){
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
            messageBox.setMessage(e.getMessage());
            messageBox.open();
        }


        result.pack();


        // Close button
        Button closeButton = new Button(shell, SWT.PUSH);
        closeButton.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,true,false));
        closeButton.setText("   Close   ");
        closeButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0){
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                shell.close();
            }
        });

        // Save File button
        Button saveButton = new Button(shell, SWT.PUSH);
        saveButton.setText("     Save to file     ");
        saveButton.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false));
        saveButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                FileDialog fd = new FileDialog(shell, SWT.SAVE);
                fd.setText("Save");
                fd.setFilterPath("C:/");
                String[] filterExt = { "*.csv" };
                fd.setFilterExtensions(filterExt);
                csvFile = fd.open();

                if (csvFile != null) {
                    try{
                        String csv = csvFile;
                        File csvFile = new File(csv);
                        PrintWriter csvTmp = new PrintWriter(
                                new BufferedWriter(
                                        new FileWriter(csvFile)));

                        File tmpFile = new File("./temp.csv");
                        BufferedReader tmp = new BufferedReader(new FileReader(tmpFile));

                        String temp;
                        while((temp=tmp.readLine())!=null){
                            csvTmp.println(temp);
                        }

                        MessageBox messageBox = new MessageBox(shell, SWT.CLOSE);
                        messageBox.setText("Success");
                        messageBox.setMessage("The CSV File was created. ( "+csvFile+" )");
                        messageBox.open();

                        tmp.close();
                        csvTmp.close();
                        tmpFile.delete();

                        shell.close();
                    }catch(IOException e){
                        System.err.println("Execution failure: "+e);
                        System.exit(1);
                    }
                }
            }
        });

        shell.pack();
        shell.setSize(shell.getSize().x, 300);
    }

}
