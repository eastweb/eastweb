package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ProjectInfo;

public class ProjectSelectionPage extends ProjectPage implements Listener, IPageChangedListener {

    private Combo projectCmb;

    protected ProjectSelectionPage(String pageName) {
        super(pageName);

        setTitle("Project");
        setMessage("Select project to add zonal summaries to.");
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout(1,false);
            layout.marginWidth = 20;
            layout.marginHeight = 20;
            layout.horizontalSpacing = 10;
            layout.verticalSpacing = 10;
            composite.setLayout(layout);
        }

        projectCmb = new Combo(composite, SWT.DROP_DOWN);
        try {
            projectCmb.setItems(Config.getInstance().getProjectNames());
            projectCmb.setText(projectCmb.getItem(0));
        } catch (Exception e) {
            // Shouldn't really ever happen.
            e.printStackTrace();
        }

        setControl(composite);
        addListeners();
    }

    private void addListeners() {
        ((WizardDialog)getWizard().getContainer()).addPageChangedListener(this);
    }

    @Override
    public void handleEvent(Event event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveToModel(ProjectInfo projectInfo) {

    }

    public String getProject() {
        return projectCmb.getText();
    }

    @Override
    public void pageChanged(PageChangedEvent event) {
        if (!projectCmb.getText().isEmpty()) {
            try {
                ((AddZonalSummariesWizard) getWizard()).setProjectInfo(Config.getInstance().loadProject(projectCmb.getText()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
