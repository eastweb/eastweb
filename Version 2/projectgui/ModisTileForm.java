package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.sdstate.eastweb.prototype.ModisTile;

/**
 * GUI for selecting Modis tiles.
 * 
 * @author Isaiah Snell-Feikema
 */
public class ModisTileForm extends Dialog implements Listener {
    private ModisTile modisTile;

    private Shell shell;
    private Composite settingsCmp;
    private Label horzLbl;
    private Label vertLbl;
    private Text horzTxt;
    private Text vertTxt;
    private Composite controlsCmp;
    private Button addBtn;
    private Button cancelBtn;

    public ModisTileForm(Shell parent) {
        super(parent);
    }

    public ModisTile open() {
        Shell parent = getParent();

        shell = new Shell(parent, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("Add Modis Tile");
        shell.setLocation(parent.getLocation().x + 50, parent.getLocation().y + 50);
        {
            GridLayout layout = new GridLayout(1, true);
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            shell.setLayout(layout);
        }

        setupWidgets();
        addListeners();

        shell.open();
        Display display = shell.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        return modisTile;
    }

    private void setupWidgets() {
        // Data entries
        settingsCmp = new Composite(shell, SWT.NONE);
        {
            GridLayout layout = new GridLayout(2, false);
            layout.marginWidth = 15;
            layout.marginHeight = 15;
            settingsCmp.setLayout(layout);
        }

        horzLbl = new Label(settingsCmp, SWT.CENTER);
        horzLbl.setText("Horizontal");
        vertLbl = new Label(settingsCmp, SWT.CENTER);
        vertLbl.setText("Vertical");

        horzTxt = new Text(settingsCmp, SWT.BORDER);
        vertTxt = new Text(settingsCmp, SWT.BORDER);

        settingsCmp.pack();


        // Separator
        Label separator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));


        // Controls
        controlsCmp = new Composite(shell, SWT.NONE);
        controlsCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        {
            FormLayout layout = new FormLayout();
            layout.marginWidth = 10;
            layout.marginHeight = 10;
            layout.spacing = 10;
            controlsCmp.setLayout(layout);
        }

        // Cancel button
        cancelBtn = new Button(controlsCmp, SWT.PUSH);
        cancelBtn.setText("    Cancel    ");
        {
            FormData data = new FormData();
            data.right = new FormAttachment(100, 0);
            cancelBtn.setLayoutData(data);
        }

        // Add button
        addBtn = new Button(controlsCmp, SWT.PUSH);
        addBtn.setText("     Add     ");
        {
            FormData data = new FormData();
            data.right = new FormAttachment(cancelBtn, 0, SWT.LEFT);
            addBtn.setLayoutData(data);
        }

        controlsCmp.pack();


        shell.pack();
    }

    private void addListeners() {
        horzTxt.addListener(SWT.Verify, new IntVerifyListener());
        vertTxt.addListener(SWT.Verify, new IntVerifyListener());
        addBtn.addListener(SWT.Selection, this);
        cancelBtn.addListener(SWT.Selection, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == addBtn) {
            if (!horzTxt.getText().isEmpty() && !vertTxt.getText().isEmpty()) {
                int horz = Integer.parseInt(horzTxt.getText());
                int vert = Integer.parseInt(vertTxt.getText());

                if (horz <= 0 || horz > 35 || vert <= 0 || vert > 17) {
                    MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                    messageBox.setMessage("Tiles must be within the range 1 to 35 horizontal and 1 to 17 vertical.");
                    messageBox.open();
                } else {
                    modisTile = new ModisTile(horz, vert);
                    shell.close();
                }
            } else {
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                messageBox.setMessage("Please ensure all parameters have been set.");
                messageBox.open();
            }
        } else if (event.widget == cancelBtn) {
            shell.close();
        }
    }
}
