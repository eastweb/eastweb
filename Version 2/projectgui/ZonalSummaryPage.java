package edu.sdstate.eastweb.prototype.projectgui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.ZonalSummary;

public class ZonalSummaryPage extends ProjectPage implements Listener, IProjectInfoChangeListener {

    private static final String TABLE_ITEM_DATA_OLD = "old";
    private static final String TABLE_ITEM_DATA_SUMMARY = "summary";

    enum Type {
        NEW_PROJECT,
        ADD_ZONAL_SUMMARY
    }

    private Type type;

    private Table summaryTbl;
    private TableColumn[] summaryCols;
    private Button addBtn;
    private Button removeBtn;

    private String problem;

    public ZonalSummaryPage(String pageName, Type type) {
        super(pageName);

        setTitle("Zonal Summaries");
        setMessage("Add zonal summaries.");

        this.type = type;
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

        summaryTbl = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
        summaryTbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        summaryTbl.setHeaderVisible(true);

        summaryCols = new TableColumn[3];

        summaryCols[0] = new TableColumn(summaryTbl, SWT.NONE);
        summaryCols[0].setText("Name");
        summaryCols[0].setWidth(80);

        summaryCols[1] = new TableColumn(summaryTbl, SWT.NONE);
        summaryCols[1].setText("Shape File");
        summaryCols[1].setWidth(160);

        summaryCols[2] = new TableColumn(summaryTbl, SWT.NONE);
        summaryCols[2].setText("Field");
        summaryCols[2].setWidth(80);

        addBtn = new Button(composite, SWT.PUSH);
        addBtn.setText("   Add   ");

        removeBtn = new Button(composite, SWT.PUSH);
        removeBtn.setText("  Remove  ");

        addListeners();
        setControl(composite);
    }

    private void addListeners() {
        summaryTbl.addListener(SWT.Selection, this);
        addBtn.addListener(SWT.Selection, this);
        removeBtn.addListener(SWT.Selection, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.widget == summaryTbl) {
            if (summaryTbl.getSelection()[0].getData(TABLE_ITEM_DATA_OLD) == null) {
                removeBtn.setEnabled(true);
            } else {
                removeBtn.setEnabled(false);
            }
        } else if (event.widget == addBtn) {
            ZonalSummaryForm form = new ZonalSummaryForm(getShell());
            ZonalSummary summary = form.open();
            if (summary != null) {
                addZonalSummary(summary, false);
            }
        } else if (event.widget == removeBtn) {
            int index = summaryTbl.getSelectionIndex();
            if (index != -1) {
                summaryTbl.remove(index);
                if (summaryTbl.getItemCount() <= 0) {
                    problem = "The project needs at least one zonal summary.";
                }
            }
        }

        setErrorMessage(getMostSevereProblem());
        getWizard().getContainer().updateButtons();
    }

    private void addZonalSummary(ZonalSummary summary, boolean old) {
        // TODO: do better.
        for (TableItem item : summaryTbl.getItems()) {
            if (item.getText(0).equals(summary.getName())) {
                return;
            }
        }

        TableItem item = new TableItem(summaryTbl, SWT.NONE);
        item.setText(
                new String[] {
                        summary.getName(),
                        summary.getShapeFile(),
                        summary.getField()
                }
        );
        item.setData(TABLE_ITEM_DATA_SUMMARY, summary);
        if (old) {
            item.setData(TABLE_ITEM_DATA_OLD, 0);
        }
    }

    private String getMostSevereProblem() {
        if (problem != null) {
            return problem;
        }

        return null;
    }

    @Override
    public boolean isPageComplete() {
        if (summaryTbl.getItemCount() <= 0) {
            return false;
        }

        return true;
    }


    @Override
    public void saveToModel(ProjectInfo projectInfo) {
        List<ZonalSummary> summaries = new ArrayList<ZonalSummary>();
        for (TableItem item : summaryTbl.getItems()) {
            summaries.add((ZonalSummary)item.getData(TABLE_ITEM_DATA_SUMMARY));
        }

        projectInfo.setZonalSummaries(summaries.toArray(new ZonalSummary[0]));
    }

    @Override
    public void projectInfoChanged(ProjectInfo newProjectInfo) {
        if (type == Type.ADD_ZONAL_SUMMARY) {
            for (TableItem item : summaryTbl.getItems()) {
                item.dispose();
            }

            for (ZonalSummary summary : newProjectInfo.getZonalSummaries()) {
                addZonalSummary(summary, true);
            }

            getWizard().getContainer().updateButtons();
        }
    }

}
