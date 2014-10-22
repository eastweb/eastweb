package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.Projection;

public class AlbersLambertParameterPage extends ProjectPage implements Listener {

    private Text standardParallel1;
    private Text standardParallel2;
    private Text centralMeridianTxt;
    private Text latitudeOfOriginTxt;
    private Text falseEastingTxt;
    private Text falseNorthingTxt;

    public AlbersLambertParameterPage(String pageName) {
        super(pageName);

        setTitle("Projection Parameters");
        setMessage("Set projection parameters of specified conic projection.");
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout(4,false);
            layout.marginWidth = 20;
            layout.marginHeight = 20;
            layout.horizontalSpacing = 10;
            layout.verticalSpacing = 15;
            composite.setLayout(layout);
        }

        new Label(composite, SWT.NONE).setText("Standard Parallel 1:");
        standardParallel1 = new Text(composite, SWT.BORDER);
        standardParallel1.setText("0.0");
        standardParallel1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText("Standard Parallel 2:");
        standardParallel2 = new Text(composite, SWT.BORDER);
        standardParallel2.setText("0.0");
        standardParallel2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText("Central meridian:");
        centralMeridianTxt = new Text(composite, SWT.BORDER);
        centralMeridianTxt.setText("0.0");
        centralMeridianTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText("Latitude of origin:");
        latitudeOfOriginTxt = new Text(composite, SWT.BORDER);
        latitudeOfOriginTxt.setText("0.0");
        latitudeOfOriginTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText("False easting:");
        falseEastingTxt = new Text(composite, SWT.BORDER);
        falseEastingTxt.setText("0.0");
        falseEastingTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        new Label(composite, SWT.NONE).setText("False northing:");
        falseNorthingTxt = new Text(composite, SWT.BORDER);
        falseNorthingTxt.setText("0.0");
        falseNorthingTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addListeners();
        setControl(composite);
    }

    private void addListeners() {
        standardParallel1.addListener(SWT.Modify, this);
        standardParallel2.addListener(SWT.Modify, this);
        centralMeridianTxt.addListener(SWT.Modify, this);
        latitudeOfOriginTxt.addListener(SWT.Modify, this);
        falseEastingTxt.addListener(SWT.Modify, this);
        falseNorthingTxt.addListener(SWT.Modify, this);
    }

    @Override
    public void handleEvent(Event event) {
        getWizard().getContainer().updateButtons();
    }

    @Override
    public boolean isPageComplete() {
        try {
            Double.parseDouble(standardParallel1.getText());
            Double.parseDouble(standardParallel2.getText());
            Double.parseDouble(centralMeridianTxt.getText());
            Double.parseDouble(latitudeOfOriginTxt.getText());
            Double.parseDouble(falseEastingTxt.getText());
            Double.parseDouble(falseNorthingTxt.getText());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void saveToModel(ProjectInfo projectInfo) {
        Projection projection = projectInfo.getProjection();

        projection.setStandardParallel1(Double.parseDouble(standardParallel1.getText()));
        projection.setStandardParallel2(Double.parseDouble(standardParallel2.getText()));
        projection.setCentralMeridian(Double.parseDouble(centralMeridianTxt.getText()));
        projection.setLatitudeOfOrigin(Double.parseDouble(latitudeOfOriginTxt.getText()));
        projection.setFalseEasting(Double.parseDouble(falseEastingTxt.getText()));
        projection.setFalseNorthing(Double.parseDouble(falseNorthingTxt.getText()));
    }

}

