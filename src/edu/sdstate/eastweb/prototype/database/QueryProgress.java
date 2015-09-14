package edu.sdstate.eastweb.prototype.database;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;


import edu.sdstate.eastweb.prototype.ConfigReadException;

public class QueryProgress extends Dialog {
    Shell shell;
    private String fields;
    private String query;
    private QueryResult result;

    public QueryProgress(Shell parent, String fields, SQL query) {
        super(parent);
        this.fields = fields;
        this.query = query.getQuery();
    }

    public void open() throws ConfigReadException, SQLException {
        shell = new Shell(getParent(), SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setText("Querying ..");
        shell.setSize(300,50);
        shell.setLayout(new FillLayout());
        //        GridLayout layout = new GridLayout();
        //        layout.marginHeight = 15;
        //        layout.marginWidth = 15;
        //        shell.setLayout(layout);

        shell.setLocation(
                getParent().getLocation().x + (getParent().getSize().x - shell.getSize().x)/2,
                getParent().getLocation().y + (getParent().getSize().y - shell.getSize().y)/2
                );

        setupWidgets();


    }

    public void setupWidgets() throws ConfigReadException, SQLException {
        Label label = new Label(shell, SWT.NONE);
        label.setText("Please wait...");

        shell.open();
        shell.pack();


        System.out.println("TRACE: creating/opening query result.");
        try {
            result = new QueryResult(shell, fields.split(","), query);
            result.open();
        } catch (Exception e) {
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            messageBox.setText("Error");
            messageBox.setMessage(e.getMessage());
            messageBox.open();
        }



        shell.close();
    }
}
