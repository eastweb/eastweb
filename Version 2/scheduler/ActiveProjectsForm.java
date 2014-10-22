package edu.sdstate.eastweb.prototype.scheduler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ProjectInfo;

public class ActiveProjectsForm extends Dialog implements Listener {

    private int result;
    private Shell shell;
    private Table projectTbl;
    private Button setBtn;
    private Button cancelBtn;

    public ActiveProjectsForm(Shell parent) {
        super(parent);
    }

    public int open() {
        shell = new Shell(getParent(), SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.setText("Set Active Projects");
        {
            GridLayout layout = new GridLayout(2, false);
            layout.marginWidth = 10;
            layout.marginHeight = 10;
            layout.verticalSpacing = 10;
            layout.horizontalSpacing = 10;
            shell.setLayout(layout);
        }

        setupWidgets();
        addListeners();

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

        return result;
    }

    private void setupWidgets() {
        new Label(shell, SWT.NONE).setText("Set which projects should be active.");

        projectTbl = new Table(shell, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
        projectTbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        populateTable();

        cancelBtn = new Button(shell, SWT.PUSH);
        cancelBtn.setText("  Cancel  ");
        cancelBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        setBtn = new Button(shell, SWT.PUSH);
        setBtn.setText("   Save   ");
        setBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        shell.pack();
        shell.setSize(shell.getSize().x, 350);
    }

    private void populateTable() {
        try {
            for (ProjectInfo project : Config.getInstance().getProjects()) {
                TableItem item = new TableItem(projectTbl, SWT.NONE);
                item.setText(project.getName());
                item.setChecked(project.isActive());
            }
        } catch (Exception e) {
            MessageBox message = new MessageBox(shell, SWT.ICON_ERROR);
            message.setMessage("Unable to load projects.");
        }
    }

    private void addListeners() {
        cancelBtn.addListener(SWT.Selection, this);
        setBtn.addListener(SWT.Selection, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == cancelBtn) {
            result = SWT.CANCEL;
            shell.close();
        } else if (event.widget == setBtn) {
            try {
                Config config = Config.getInstance();
                for (TableItem item : projectTbl.getItems()) {
                    ProjectInfo project = config.loadProject(item.getText());
                    project.setActive(item.getChecked());
                    config.saveProject(project);
                }
                result = SWT.OK;
            } catch (Exception e) {
                MessageBox message = new MessageBox(shell, SWT.ICON_ERROR);
                message.setMessage("Unable to save changes.");
                result = SWT.CANCEL;
            }
            shell.close();
        }
    }

}
