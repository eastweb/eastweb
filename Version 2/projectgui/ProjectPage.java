package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.jface.wizard.WizardPage;

import edu.sdstate.eastweb.prototype.ProjectInfo;

public abstract class ProjectPage extends WizardPage {

    protected ProjectPage(String pageName) {
        super(pageName);
    }

    public abstract void saveToModel(ProjectInfo projectInfo);

}
