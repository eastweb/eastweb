package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.Projection;
import edu.sdstate.eastweb.prototype.Projection.Datum;
import edu.sdstate.eastweb.prototype.Projection.ProjectionType;
import edu.sdstate.eastweb.prototype.Projection.ResamplingType;



/**
 * 
 * 
 * @author Isaiah Snell-Feikema
 */
class ProjectionPage extends ProjectPage implements Listener {

    private static final String ALBERS_EQUAL_AREA = "Albers Equal Area";
    private static final String LAMBERT_CONFORMAL_CONIC = "Lambert Conformal Conic";
    private static final String TRANSVERSE_MERCATOR = "Transverse Mercator";

    private static final String NAD83 = "NAD83";
    private static final String NAD27 = "NAD27";
    private static final String WGS84 = "WGS84";
    private static final String WGS72 = "WGS72";

    private static final String NEAREST_NEIGHBOR = "Nearest Neighbor";
    private static final String BILINEAR = "Bilinear";
    private static final String CUBIC_CONVOLUTION = "Cubic Convolution";

    private Combo projectionCmb;
    private Combo resamplingCmb;
    private Combo datumCmb;
    private Text pixelSizeTxt;

    public ProjectionPage(String name) {
        super(name);

        setTitle("Projection");
        setMessage("Set projection settings.");
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

        new Label(composite, SWT.NONE).setText("Coordinate system: ");
        projectionCmb = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        projectionCmb.setItems(
                new String[] {
                        ALBERS_EQUAL_AREA,
                        LAMBERT_CONFORMAL_CONIC,
                        TRANSVERSE_MERCATOR
                }
        );

        new Label(composite, SWT.NONE).setText("Resampling type: ");
        resamplingCmb = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        resamplingCmb.setItems(
                new String[] {
                        NEAREST_NEIGHBOR,
                        BILINEAR,
                        CUBIC_CONVOLUTION
                }
        );

        new Label(composite, SWT.NONE).setText("Datum: ");
        datumCmb = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        datumCmb.setItems(
                new String[] {
                        NAD83,
                        NAD27,
                        WGS84,
                        WGS72,
                }
        );

        new Label(composite, SWT.NONE).setText("Pixel size in meters: ");
        pixelSizeTxt = new Text(composite, SWT.BORDER);

        addListeners();
        setControl(composite);
    }

    private void addListeners() {
        projectionCmb.addListener(SWT.Selection, this);
        resamplingCmb.addListener(SWT.Selection, this);
        datumCmb.addListener(SWT.Selection, this);
        pixelSizeTxt.addListener(SWT.Verify, new IntVerifyListener());
        pixelSizeTxt.addListener(SWT.Modify, this);
        pixelSizeTxt.addListener(SWT.FocusOut, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == pixelSizeTxt && event.type == SWT.FocusOut) {
            if (!pixelSizeTxt.getText().isEmpty()) {
                int pixelSize = Integer.parseInt(pixelSizeTxt.getText());
                if (pixelSize < 1) {
                    pixelSizeTxt.setText("1");
                }
            } else {
                pixelSizeTxt.setText("1000");
            }
        }

        getWizard().getContainer().updateButtons();
    }


    @Override
    public boolean isPageComplete() {
        if (projectionCmb.getSelectionIndex() == -1 ||
                resamplingCmb.getSelectionIndex() == -1 ||
                datumCmb.getSelectionIndex() == -1 ||
                pixelSizeTxt.getText().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public void saveToModel(ProjectInfo projectInfo) {
        Projection projection = projectInfo.getProjection();

        projection.setProjectionType(getProjectionType());

        String resampling = resamplingCmb.getText();
        if (resampling.equals(NEAREST_NEIGHBOR)) {
            projection.setResamplingType(ResamplingType.NEAREST_NEIGHBOR);
        } else if (resampling.equals(BILINEAR)) {
            projection.setResamplingType(ResamplingType.BILINEAR);
        } else if (resampling.equals(CUBIC_CONVOLUTION)) {
            projection.setResamplingType(ResamplingType.CUBIC_CONVOLUTION);
        }

        String datum = datumCmb.getText();
        if (datum.equals(NAD83)) {
            projection.setDatum(Datum.NAD83);
        } else if (datum.equals(NAD27)) {
            projection.setDatum(Datum.NAD27);
        } else if (datum.equals(WGS72)) {
            projection.setDatum(Datum.WGS72);
        } else if (datum.equals(WGS84)) {
            projection.setDatum(Datum.WGS84);
        }

        projection.setPixelSize(Integer.parseInt(pixelSizeTxt.getText()));
    }

    public ProjectionType getProjectionType() {
        String projection = projectionCmb.getText();
        if (projection.equals(ALBERS_EQUAL_AREA)) {
            return ProjectionType.ALBERS_EQUAL_AREA;
        } else if (projection.equals(LAMBERT_CONFORMAL_CONIC)) {
            return ProjectionType.LAMBERT_CONFORMAL_CONIC;
        } else if (projection.equals(TRANSVERSE_MERCATOR)) {
            return ProjectionType.TRANSVERSE_MERCATOR;
        } else {
            return null;
        }
    }

}
