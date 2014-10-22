package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.jface.wizard.*;

import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.Projection.ProjectionType;

public class NewProjectWizard extends Wizard {

    private static String BASIC_INFO_PAGE = "Basic Info";
    private static String MODIS_TILE_PAGE = "Modis Tile";
    private static String PROJECTION_PAGE = "Projection";
    private static String MERCATOR_PARAMETER_PAGE = "Mercator Parameters";
    private static String ALBERS_LAMBERT_PARAMETER_PAGE = "Albers and Lambert Parameters";
    private static String ZONAL_SUMMARY_PAGE = "Zonal Summary";

    public ProjectInfo projectInfo;

    public NewProjectWizard() {
        projectInfo = new ProjectInfo();

        setWindowTitle("New Project");
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        addPage(new BasicInfoPage(BASIC_INFO_PAGE));
        addPage(new ModisTilePage(MODIS_TILE_PAGE));
        addPage(new ProjectionPage(PROJECTION_PAGE));
        addPage(new MercatorParameterPage(MERCATOR_PARAMETER_PAGE));
        addPage(new AlbersLambertParameterPage(ALBERS_LAMBERT_PARAMETER_PAGE));
        addPage(new ZonalSummaryPage(ZONAL_SUMMARY_PAGE, ZonalSummaryPage.Type.NEW_PROJECT));
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        IWizardPage newPage = null;
        if (page.getName().equals(PROJECTION_PAGE)) {
            ProjectionPage projectionPage = (ProjectionPage)page;
            if (projectionPage.getProjectionType() != null) {
                if (projectionPage.getProjectionType() == ProjectionType.TRANSVERSE_MERCATOR) {
                    newPage = getPage(MERCATOR_PARAMETER_PAGE);
                } else {
                    newPage = getPage(ALBERS_LAMBERT_PARAMETER_PAGE);
                }
            }
        } else if (page.getName().equals(MERCATOR_PARAMETER_PAGE)) {
            newPage = getPage(ZONAL_SUMMARY_PAGE);
        } else {
            newPage = super.getNextPage(page);
        }

        return newPage;
    }


    @Override
    public boolean canFinish() {
        boolean canFinish = false;
        IWizardPage page = getPage(BASIC_INFO_PAGE);
        while (page != null && page.isPageComplete()) {
            if (page.getName().equals(ZONAL_SUMMARY_PAGE)) {
                if (page.isPageComplete()) {
                    canFinish = true;
                }
            }

            page = getNextPage(page);
        }

        return canFinish;
    }

    @Override
    public boolean performFinish() {
        IWizardPage page = getPage(BASIC_INFO_PAGE);
        while (page != null && page.isPageComplete()) {
            ((ProjectPage)page).saveToModel(projectInfo);
            page = getNextPage(page);
        }

        return true;
    }

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

}
