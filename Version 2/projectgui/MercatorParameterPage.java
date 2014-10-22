package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.Projection;

public class MercatorParameterPage extends ProjectPage implements Listener {

    private static final String NORTH = "North";
    private static final String SOUTH = "South";

    private Composite utmCmp;
    private Text utmTxt;
    private Combo utmCmb;
    private Composite parameterCmp;
    private Text centralMeridianTxt;
    private Text latitudeOfOriginTxt;
    private Text falseEastingTxt;
    private Text falseNorthingTxt;
    private Text scalingFactorTxt;

    public MercatorParameterPage(String pageName) {
        super(pageName);

        setTitle("Projection Parameters");
        setMessage("Set parameters of transverse Mercator projection manually or by UTM zone.");
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.marginWidth = 0;
            layout.marginHeight = 0;
            layout.horizontalSpacing = 0;
            layout.verticalSpacing = 0;
            composite.setLayout(layout);
        }

        utmCmp = new Composite(composite, SWT.NONE);
        {
            GridLayout layout = new GridLayout(3,false);
            layout.marginWidth = 20;
            layout.marginHeight = 20;
            layout.horizontalSpacing = 10;
            layout.verticalSpacing = 15;
            utmCmp.setLayout(layout);
        }

        new Label(utmCmp, SWT.NONE).setText("UTM zone:");
        utmTxt = new Text(utmCmp, SWT.BORDER);
        {
            GridData data = new GridData();
            data.widthHint = 15;
            utmTxt.setLayoutData(data);
        }

        utmCmb = new Combo(utmCmp, SWT.DROP_DOWN | SWT.SINGLE);
        utmCmb.setItems(
                new String[] {
                        NORTH,
                        SOUTH
                }
        );
        utmCmb.setText(NORTH);


        new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));


        parameterCmp = new Composite(composite, SWT.NONE);
        {
            GridLayout layout = new GridLayout(4,false);
            layout.marginWidth = 20;
            layout.marginHeight = 20;
            layout.horizontalSpacing = 10;
            layout.verticalSpacing = 15;
            parameterCmp.setLayout(layout);
        }

        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.minimumWidth = 70;

        new Label(parameterCmp, SWT.NONE).setText("Central meridian:");
        centralMeridianTxt = new Text(parameterCmp, SWT.BORDER);
        centralMeridianTxt.setText("0.0");
        centralMeridianTxt.setLayoutData(data);

        new Label(parameterCmp, SWT.NONE).setText("Latitude of origin:");
        latitudeOfOriginTxt = new Text(parameterCmp, SWT.BORDER);
        latitudeOfOriginTxt.setText("0.0");
        latitudeOfOriginTxt.setLayoutData(data);

        new Label(parameterCmp, SWT.NONE).setText("False easting:");
        falseEastingTxt = new Text(parameterCmp, SWT.BORDER);
        falseEastingTxt.setText("0.0");
        falseEastingTxt.setLayoutData(data);

        new Label(parameterCmp, SWT.NONE).setText("False northing:");
        falseNorthingTxt = new Text(parameterCmp, SWT.BORDER);
        falseNorthingTxt.setText("0.0");
        falseNorthingTxt.setLayoutData(data);

        new Label(parameterCmp, SWT.NONE).setText("Scaling Factor:");
        scalingFactorTxt = new Text(parameterCmp, SWT.BORDER);
        scalingFactorTxt.setText("0.0");
        scalingFactorTxt.setLayoutData(data);

        addListeners();
        setControl(composite);
    }

    private void addListeners() {
        //utmTxt.addListener(SWT.Verify, new IntVerifyListener());
        utmTxt.addListener(SWT.Modify, this);
        utmCmb.addListener(SWT.Modify, this);
        //        semiMajorAxisTxt.addListener(SWT.Modify, this);
        //        semiMinorAxisTxt.addListener(SWT.Modify, this);
        centralMeridianTxt.addListener(SWT.Modify, this);
        latitudeOfOriginTxt.addListener(SWT.Modify, this);
        falseEastingTxt.addListener(SWT.Modify, this);
        falseNorthingTxt.addListener(SWT.Modify, this);
        scalingFactorTxt.addListener(SWT.Modify, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == utmTxt || event.widget == utmCmb) {
            if (!utmTxt.getText().isEmpty()) {
                if (event.widget == utmTxt) {
                    int zone = Integer.parseInt(utmTxt.getText());
                    if (zone < 1) {
                        utmTxt.setText("1");
                    } else if (zone > 60) {
                        utmTxt.setText("60");
                    }
                }

                setParametersFromUTMZone();
            }
        }

        getWizard().getContainer().updateButtons();
    }

    private void setParametersFromUTMZone() {
        int zone = Integer.parseInt(utmTxt.getText());
        boolean northern = utmCmb.getText().equals(NORTH);

        double centralMeridian = -177 + 6*(zone-1);
        double falseNorthing = northern ? 0.0 : 10000000.0;

        //        semiMajorAxisTxt.setText("0.0");
        //        semiMinorAxisTxt.setText("0.0");
        scalingFactorTxt.setText("0.9996");
        centralMeridianTxt.setText(Double.toString(centralMeridian));
        latitudeOfOriginTxt.setText("0.0");
        falseEastingTxt.setText("500000.0");
        falseNorthingTxt.setText(Double.toString(falseNorthing));
    }

    @Override
    public boolean isPageComplete() {
        try {
            Double.parseDouble(centralMeridianTxt.getText());
            Double.parseDouble(latitudeOfOriginTxt.getText());
            Double.parseDouble(falseEastingTxt.getText());
            Double.parseDouble(falseNorthingTxt.getText());
            Double.parseDouble(scalingFactorTxt.getText());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    public void saveToModel(ProjectInfo projectInfo) {
        Projection projection = projectInfo.getProjection();

        projection.setCentralMeridian(Double.parseDouble(centralMeridianTxt.getText()));
        projection.setLatitudeOfOrigin(Double.parseDouble(latitudeOfOriginTxt.getText()));
        projection.setFalseEasting(Double.parseDouble(falseEastingTxt.getText()));
        projection.setFalseNorthing(Double.parseDouble(falseNorthingTxt.getText()));
        projection.setScalingFactor(Double.parseDouble(scalingFactorTxt.getText()));
    }

}
