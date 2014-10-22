package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.ogr;
import org.gdal.ogr.Layer;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.DataSource;

import edu.sdstate.eastweb.prototype.ZonalSummary;
import edu.sdstate.eastweb.prototype.util.GdalUtils;



/**
 * GUI for specifying zonal summary information.
 * 
 * @author Isaiah Snell-Feikema
 * @author Daniel Woodward
 */
public class ZonalSummaryForm extends Dialog implements Listener {

    private ZonalSummary zonalSummary;

    private Shell shell;
    private Label nameLbl;
    private Text nameTxt;
    private Label shapefileLbl;
    private Text shapefileTxt;
    private Button shapefileBtn;
    private Label fieldLbl;
    private Combo fieldCmb;
    private Button addBtn;
    private Button cancelBtn;

    public ZonalSummaryForm(Shell parent) {
        super(parent);
    }

    public ZonalSummary open() {
        Shell parent = getParent();

        shell = new Shell(parent, SWT.CLOSE | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("Add zonal summary");
        shell.setLocation(parent.getLocation().x + 50, parent.getLocation().y + 50);
        {
            GridLayout layout = new GridLayout(1, true);
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            layout.verticalSpacing = 0;
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

        return zonalSummary;
    }

    private void setupWidgets() {
        // Zonal summary settings
        final Composite settingsCmp = new Composite(shell, SWT.NONE);
        settingsCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        {
            GridLayout layout = new GridLayout(3, false);
            layout.marginWidth = 15;
            layout.marginHeight = 15;
            layout.horizontalSpacing = 15;
            settingsCmp.setLayout(layout);
        }

        // Name label
        nameLbl = new Label(settingsCmp, SWT.NONE);
        nameLbl.setText("Name:");

        // Name text entry
        nameTxt = new Text(settingsCmp, SWT.BORDER);
        {
            GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1);
            nameTxt.setLayoutData(data);
        }


        // Shapefile label
        shapefileLbl = new Label(settingsCmp, SWT.NONE);
        shapefileLbl.setText("Shapefile:");

        // Shapefile text
        shapefileTxt = new Text(settingsCmp, SWT.BORDER);
        shapefileTxt.setEditable(false);
        {
            GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
            data.minimumWidth = 100;
            shapefileTxt.setLayoutData(data);
        }

        // Shapefile button
        shapefileBtn = new Button(settingsCmp, SWT.PUSH);
        shapefileBtn.setText(" Choose ");


        // Field label
        fieldLbl = new Label(settingsCmp, SWT.NONE);
        fieldLbl.setText("Field:");

        // Field combo
        fieldCmb = new Combo(settingsCmp, SWT.READ_ONLY);
        fieldCmb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));


        new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


        // Form controls
        final Composite controlCmp = new Composite(shell, SWT.NONE);
        controlCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        {
            FormLayout layout = new FormLayout();
            layout.marginWidth = 10;
            layout.marginHeight = 10;
            layout.spacing = 10;
            controlCmp.setLayout(layout);
        }

        // Cancel button
        cancelBtn = new Button(controlCmp, SWT.PUSH);
        cancelBtn.setText("    Cancel    ");
        {
            FormData data = new FormData();
            data.right = new FormAttachment(100, 0);
            cancelBtn.setLayoutData(data);
        }

        // Add button
        addBtn = new Button(controlCmp, SWT.PUSH);
        addBtn.setText("     Add     ");
        {
            FormData data = new FormData();
            data.right = new FormAttachment(cancelBtn, 0, SWT.LEFT);
            addBtn.setLayoutData(data);
        }

        controlCmp.pack();
        settingsCmp.pack();
        shell.pack();
    }

    private void addListeners() {
        nameTxt.addListener(SWT.Verify, this);
        shapefileBtn.addListener(SWT.Selection, this);
        addBtn.addListener(SWT.Selection, this);
        cancelBtn.addListener(SWT.Selection, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == nameTxt) {
            char[] chars = new char[event.text.length()];
            event.text.getChars(0, chars.length, chars, 0);
            for (int i=0; i<chars.length; i++) {
                if (chars[i] == '<' || chars[i] == '>') {
                    event.doit = false;
                    return;
                }
            }
        } else if (event.widget == shapefileBtn) {
            FileDialog dialog = new FileDialog(shell, SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"*.shp"});

            String filename = dialog.open();
            if (filename != null) {
                shapefileTxt.setText(filename);

                GdalUtils.register();
                DataSource shapefile;
                synchronized (GdalUtils.lockObject) {
                    shapefile = ogr.Open(filename);
                }
                if (shapefile == null) {
                    MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                    messageBox.setMessage("Invalid shapefile!");
                    messageBox.open();
                } else {
                    synchronized (GdalUtils.lockObject) {
                        for (int iLayer=0; iLayer<shapefile.GetLayerCount(); iLayer++) {
                            Layer layer = shapefile.GetLayer(iLayer);
                            FeatureDefn layerDefn = layer.GetLayerDefn();

                            fieldCmb.removeAll();
                            for (int iFeature=0; iFeature<layerDefn.GetFieldCount(); iFeature++) {
                                FieldDefn fieldDefn = layerDefn.GetFieldDefn(iFeature);
                                String type = fieldDefn.GetFieldTypeName(fieldDefn.GetFieldType()).toLowerCase();
                                if (type.equals("string") || type.equals("integer")) { // FIXME: test! does this always work?
                                    fieldCmb.add(layerDefn.GetFieldDefn(iFeature).GetName());
                                }
                            }
                        }
                    }
                }
            }
        } else if (event.widget == addBtn) {
            int index = fieldCmb.getSelectionIndex();
            if (index != -1 && !nameTxt.getText().isEmpty() && !shapefileTxt.getText().isEmpty()) {
                zonalSummary = new ZonalSummary(
                        nameTxt.getText(),
                        shapefileTxt.getText(),
                        fieldCmb.getItem(index)
                        );

                shell.close();
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
