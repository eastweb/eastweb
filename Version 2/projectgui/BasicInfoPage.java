package edu.sdstate.eastweb.prototype.projectgui;

import java.io.File;
import java.util.Calendar;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.ProjectInfo;



/**
 * 
 * 
 * @author Isaiah Snell-Feikema
 */
class BasicInfoPage extends ProjectPage implements Listener {
    private Text nameTxt;
    private DateTime startDateDtTm;
    private Button etaBtn;
    private Text elevationTxt;
    private Button elevationBtn;
    private Text watermaskTxt;
    private Button watermaskBtn;
    private Text minLstTxt;
    private Text maxLstTxt;

    private String nameProblem;
    private String elevationProblem;
    private String watermaskProblem;
    private String lstProblem;

    public BasicInfoPage(String name) {
        super(name);

        setTitle("Basic Project Information");
        setMessage("Enter basic project information.");
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout(3,false);
            layout.marginWidth = 20;
            layout.marginHeight = 20;
            layout.horizontalSpacing = 10;
            layout.verticalSpacing = 10;
            composite.setLayout(layout);
        }

        new Label(composite, SWT.NONE).setText("Project name: ");
        nameTxt = new Text(composite, SWT.BORDER);
        nameTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

        new Label(composite, SWT.NONE).setText("Start date: ");
        startDateDtTm = new DateTime(composite, SWT.MEDIUM | SWT.BORDER);
        startDateDtTm.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

        new Label(composite, SWT.NONE).setText("Calculate ETa: ");
        etaBtn = new Button(composite, SWT.CHECK);
        etaBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

        new Label(composite, SWT.NONE).setText("Elevation file: ");
        elevationTxt = new Text(composite, SWT.BORDER);
        elevationTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        elevationTxt.setEnabled(false);
        elevationBtn = new Button(composite, SWT.PUSH);
        elevationBtn.setText("   Browse   ");
        elevationBtn.setEnabled(false);

        new Label(composite, SWT.NONE).setText("Watermask: ");
        watermaskTxt = new Text(composite, SWT.BORDER);
        watermaskTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        watermaskBtn = new Button(composite, SWT.PUSH);
        watermaskBtn.setText("   Browse   ");

        new Label(composite, SWT.NONE).setText("Minimum LST in celcius:");
        minLstTxt = new Text(composite, SWT.BORDER);
        minLstTxt.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 2, 1));
        minLstTxt.setText("-123.15");

        new Label(composite, SWT.NONE).setText("Maximum LST in celcius:");
        maxLstTxt = new Text(composite, SWT.BORDER);
        maxLstTxt.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 2, 1));
        maxLstTxt.setText("1037.55");

        addListeners();
        setControl(composite);
    }

    protected void addListeners() {
        nameTxt.addListener(SWT.Modify, this);
        nameTxt.addListener(SWT.Verify, this);
        startDateDtTm.addListener(SWT.Selection, this);
        etaBtn.addListener(SWT.Selection, this);
        elevationTxt.addListener(SWT.Modify, this);
        elevationBtn.addListener(SWT.Selection, this);
        watermaskTxt.addListener(SWT.Modify, this);
        watermaskBtn.addListener(SWT.Selection, this);
        minLstTxt.addListener(SWT.Modify, this);
        maxLstTxt.addListener(SWT.Modify, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == nameTxt) {
            if (event.type == SWT.Modify) {
                File file;
                try {
                    file = Config.getInstance().getProjectFilename(nameTxt.getText());
                    if (file.exists()) {
                        nameProblem = "The specified project name conflicts with an already existing project.";
                    } else {
                        nameProblem = null;
                    }
                } catch (ConfigReadException e) {
                    e.printStackTrace();
                }
            } else if (event.type == SWT.Verify) {
                String string = event.text;
                char[] chars = new char[string.length()];
                string.getChars(0, chars.length, chars, 0);
                verify:
                    for (int i=0; i<chars.length; i++) {
                        if (!(Character.isLetterOrDigit(chars[i]) ||
                                Character.isWhitespace(chars[i]) ||
                                chars[i] == '_')) {
                            event.doit = false;
                            break verify;
                        }
                    }
            }
        } else if (event.widget == startDateDtTm) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            if (startDateDtTm.getDay() > day) {
                startDateDtTm.setDay(day);
            } else if (startDateDtTm.getMonth() > month) {
                startDateDtTm.setMonth(month);
            } else if (startDateDtTm.getYear() > year) {
                startDateDtTm.setYear(year);
            }

            // TODO: find earliest date we support.

        } else if (event.widget == etaBtn) {
            if (etaBtn.getSelection()) {
                elevationTxt.setEnabled(true);
                elevationBtn.setEnabled(true);
            } else {
                elevationTxt.setEnabled(false);
                elevationBtn.setEnabled(false);
            }
        } else if (event.widget == elevationBtn) {
            FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"*.tif"});
            String path = dialog.open();

            if (path != null) {
                elevationTxt.setText(path);
            }
        } else if (event.widget == watermaskBtn) {
            FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"*.tif"});
            String path = dialog.open();

            if (path != null) {
                watermaskTxt.setText(path);
            }
        } else if (event.widget == elevationTxt) {
            if (elevationTxt.getText().isEmpty()) {
                elevationProblem = null;
            } else {
                File file = new File(elevationTxt.getText());
                if (!file.exists() || !file.isFile()) {
                    elevationProblem = "Invalid elevation filepath.";
                } else {
                    elevationProblem = null;
                }
            }
        } else if (event.widget == watermaskTxt) {
            File file = new File(watermaskTxt.getText());
            if (!file.exists() || !file.isFile()) {
                watermaskProblem = "Invalid watermask filepath.";
            } else {
                watermaskProblem = null;
            }
        } else if (event.widget == minLstTxt || event.widget == maxLstTxt) {
            boolean areDoubles = true;
            try {
                Double.parseDouble(minLstTxt.getText());
                Double.parseDouble(maxLstTxt.getText());
            } catch (Exception e) {
                areDoubles = false;
            }

            if (minLstTxt.getText().trim().isEmpty() || maxLstTxt.getText().trim().isEmpty()) {
                lstProblem = "Minimum and maximum LST values are required.";
            } else if (!areDoubles) {
                lstProblem = "Minimum and maximum LST values must be real numbers.";
            } else if (Double.parseDouble(minLstTxt.getText()) > Double.parseDouble(maxLstTxt.getText())) {
                lstProblem = "Minimum LST must be less than maximum LST.";
            } else {
                lstProblem = null;
            }
        }

        setErrorMessage(getMostSevereProblem());
        getWizard().getContainer().updateButtons();
    }

    private String getMostSevereProblem() {
        if (nameProblem != null) {
            return nameProblem;
        } else if (etaBtn.getSelection() && elevationProblem != null) {
            return elevationProblem;
        } else if (watermaskProblem != null) {
            return watermaskProblem;
        } else if (lstProblem != null) {
            return lstProblem;
        }

        return null;
    }

    @Override
    public boolean isPageComplete() {
        if (nameTxt.getText().isEmpty() || nameProblem != null ||
                watermaskTxt.getText().isEmpty() || watermaskProblem != null ||
                lstProblem != null) {
            return false;
        }

        if (etaBtn.getSelection() && (elevationTxt.getText().isEmpty() || elevationProblem != null)) {
            return false;
        }

        return true;
    }

    @Override
    public void saveToModel(ProjectInfo project) {
        project.setName(nameTxt.getText());
        project.setStartDate(
                new DataDate(
                        startDateDtTm.getDay(),
                        startDateDtTm.getMonth()+1, // Convert from starting at zero to starting at one.
                        startDateDtTm.getYear()
                        )
                );
        project.setShouldCalculateETa(etaBtn.getSelection());
        project.setElevation(elevationTxt.getText());
        project.setWatermask(watermaskTxt.getText());
        project.setMinLst(Double.parseDouble(minLstTxt.getText()));
        project.setMaxLst(Double.parseDouble(maxLstTxt.getText()));
    }

}