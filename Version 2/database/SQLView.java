package edu.sdstate.eastweb.prototype.database;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class SQLView extends Dialog {
    Shell shell;
    Text sql;
    String query;

    public SQLView(Shell parent, String query) {
        super(parent);

        this.query = query;
    }

    public void open() {
        shell = new Shell(getParent(), SWT.CLOSE | SWT.APPLICATION_MODAL);
        shell.setText("SQL View");

        setupWidgets();

        shell.setSize(350, 435);
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
        sql = new Text(shell, SWT.BORDER | SWT.V_SCROLL);
        sql.setBounds(5, 5, 100, 100);
        sql.setSize(335, 400);
        sql.setText(query);

        shell.pack();
    }

}

