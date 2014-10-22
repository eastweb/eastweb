package edu.sdstate.eastweb.prototype.projectgui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ProjectInfo;

public class AddZonalSummariesWizard extends Wizard {

    public static final String PROJECT_SELECTION_PAGE = "Project Selection";
    public static final String ZONAL_SUMMARY_PAGE = "Zonal Summaries";

    private ProjectInfo projectInfo;
    private List<IProjectInfoChangeListener> projectInfoListeners = new ArrayList<IProjectInfoChangeListener>();

    public void setProjectInfo(ProjectInfo projectInfo) {
        if (this.projectInfo == null || !projectInfo.getName().equals(this.projectInfo.getName())) {
            this.projectInfo = projectInfo;

            for (IProjectInfoChangeListener listener : projectInfoListeners) {
                listener.projectInfoChanged(projectInfo);
            }
        }
    }

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    @Override
    public void addPages() {
        addPage(new ProjectSelectionPage(PROJECT_SELECTION_PAGE));
        ZonalSummaryPage page = new ZonalSummaryPage(ZONAL_SUMMARY_PAGE, ZonalSummaryPage.Type.ADD_ZONAL_SUMMARY);
        projectInfoListeners.add(page);
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        IWizardPage page = getPage(PROJECT_SELECTION_PAGE);
        while (page != null && page.isPageComplete()) {
            ((ProjectPage)page).saveToModel(projectInfo);
            page = getNextPage(page);
        }

        return true;
    }

    public static void main(String args[]) throws Exception {
        Display display = new Display();
        Shell shell = new Shell(display);

        AddZonalSummariesWizard wizard = new AddZonalSummariesWizard();

        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.setBlockOnOpen(true);
        int status = dialog.open();

        if (status == WizardDialog.OK) {
            Config.getInstance().saveProject(wizard.getProjectInfo());
        }

        shell.dispose();
        display.dispose();
    }

}
