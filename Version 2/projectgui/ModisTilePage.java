package edu.sdstate.eastweb.prototype.projectgui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import edu.sdstate.eastweb.prototype.ModisTile;
import edu.sdstate.eastweb.prototype.ProjectInfo;


public class ModisTilePage extends ProjectPage implements Listener {

    private Map<String, ModisTile> modisTiles;

    private List modisTileLst;
    private Button addBtn;
    private Button removeBtn;

    private String problem;

    public ModisTilePage(String pageName) {
        super(pageName);

        setTitle("Modis Tiles");
        setMessage("Select modis tiles to be used in project.");

        modisTiles = new HashMap<String, ModisTile>();
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout(2,false);
            layout.marginWidth = 20;
            layout.marginHeight = 20;
            layout.horizontalSpacing = 10;
            layout.verticalSpacing = 10;
            composite.setLayout(layout);
        }

        modisTileLst = new List(composite, SWT.BORDER | SWT.V_SCROLL);
        modisTileLst.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        addBtn = new Button(composite, SWT.PUSH);
        addBtn.setText("   Add   ");

        removeBtn = new Button(composite, SWT.PUSH);
        removeBtn.setText("  Remove  ");

        addListeners();
        setControl(composite);
    }

    private void addListeners() {
        addBtn.addListener(SWT.Selection, this);
        removeBtn.addListener(SWT.Selection, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == addBtn) {
            ModisTileForm form = new ModisTileForm(getShell());
            ModisTile tile = form.open();
            if (tile != null) {
                String newItem = String.format("h%02dv%02d", tile.getHTile(), tile.getVTile());

                boolean duplicate = false;
                for (String item : modisTileLst.getItems()) {
                    if (item.equals(newItem)) {
                        duplicate = true;
                        break;
                    }
                }

                if (!duplicate) {
                    modisTileLst.add(newItem);
                    modisTiles.put(newItem, tile);

                    problem = null;
                } else {
                    MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
                    messageBox.setMessage("The project's modis tile list already contains this modis tile.");
                    messageBox.open();
                }
            }
        } else if (event.widget == removeBtn) {
            String selection = modisTileLst.getSelection()[0];
            if (selection != null) {
                modisTileLst.remove(selection);
                modisTiles.remove(selection);

                if (modisTileLst.getItemCount() <= 0) {
                    problem = "The project needs at least one modis tile.";
                }
            }
        }

        setErrorMessage(getMostSevereProblem());
        getWizard().getContainer().updateButtons();
    }

    private String getMostSevereProblem() {
        if (problem != null) {
            return problem;
        }

        return null;
    }

    @Override
    public boolean isPageComplete() {
        if (modisTileLst.getItemCount() <= 0) {
            return false;
        }

        if (problem != null) {
            return false;
        }

        return true;
    }

    @Override
    public void saveToModel(ProjectInfo projectInfo) {
        projectInfo.setModisTiles(modisTiles.values().toArray(new ModisTile[0]));
    }

}
