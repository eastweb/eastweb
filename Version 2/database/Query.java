package edu.sdstate.eastweb.prototype.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.indices.EnvironmentalIndex;

public class Query extends Dialog {

    final String[] CONDITIONS = {" = "," <= ", " >= ", " < ", " > ", " BETWEEN "};

    SQL query;
    String field;
    String where;

    Shell shell;
    Combo projCombo;
    Button zoneButton;
    Label zoneLabel;
    Combo zoneCombo;
    Text[] zoneText;
    Button yearButton;
    Label yearLabel;
    Combo yearCombo;
    Text[] yearText;
    Button dayButton;
    Label dayLabel;
    Combo dayCombo;
    Text[] dayText;
    Group envGroup;
    Button envButton;
    List envList1;
    List envList2;
    Button in;
    Button out;

    Button sql;
    Button[] fieldsButton;
    Button[] aveFields;

    public Query(Shell parent) {
        super(parent);
        field = ",name,year,day,count,sum,mean,stdev";
        where = "";
        query = null;
    }

    public void open() {
        shell = new Shell(getParent(), SWT.CLOSE | SWT.APPLICATION_MODAL);
        shell.setText("Database Query");
        shell.setLayout(new FormLayout());

        setupWidgets();

        shell.setLocation(
                getParent().getLocation().x + getParent().getSize().x/2 - shell.getSize().x/2,
                getParent().getLocation().y + getParent().getSize().y/2 - shell.getSize().y/2
                );

        shell.open();
        Display display = shell.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    public void setupWidgets() {
        // Composite for 'search' and 'cancel' buttons.
        Composite controlsComposite = new Composite(shell, SWT.BORDER);
        {
            FormData data = new FormData();
            data.right = new FormAttachment(100,0);
            data.left = new FormAttachment(0,0);
            data.bottom = new FormAttachment(100,0);
            controlsComposite.setLayoutData(data);
        }
        {
            FormLayout layout = new FormLayout();
            layout.marginWidth = 10;
            layout.marginHeight = 10;
            layout.spacing = 10;
            controlsComposite.setLayout(layout);
        }

        // Cancel button
        Button cancelButton = new Button(controlsComposite, SWT.PUSH);
        cancelButton.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,true));
        cancelButton.setText("   Cancel   ");
        {
            FormData data = new FormData();
            data.right = new FormAttachment(100,0);
            cancelButton.setLayoutData(data);
        }
        cancelButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0){
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                shell.close();
            }
        });

        // Search button
        Button searchButton = new Button(controlsComposite, SWT.PUSH);
        searchButton.setText("     Search     ");
        {
            FormData data = new FormData();
            data.right = new FormAttachment(cancelButton,0,SWT.LEFT);
            searchButton.setLayoutData(data);
        }
        searchButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                boolean check = false;

                if(envButton.getSelection()) {
                    check=true;
                } else{
                    for(int i=0;i<fieldsButton.length;i++) {
                        if(fieldsButton[i].getSelection()) {
                            check = true;
                        }
                    }
                }

                if(check){
                    where = "";
                    if(zoneButton.getSelection()){
                        if(zoneCombo.getText().equals(CONDITIONS[5])){
                            if(isStringInt(zoneText[0].getText())&&isStringInt(zoneText[1].getText())) {
                                //where = where + ";(\"zoneID\"" + CONDITIONS[5] + zoneText[0].getText() + " AND " + zoneText[1].getText() + ")";
                                where += String.format(
                                        ";(name::int %s %d AND %d)",
                                        zoneCombo.getText(),
                                        Integer.parseInt(zoneText[0].getText()),
                                        Integer.parseInt(zoneText[1].getText())
                                        );
                            } else{
                                check = false;
                            }
                        }
                        else{
                            if(isStringInt(zoneText[0].getText())) {
                                //where = where + ";(\"zoneID\"" + zoneCombo.getText() + zoneText[0].getText() + ")";
                                where += String.format(
                                        ";(name::int %s %d)",
                                        zoneCombo.getText(),
                                        Integer.parseInt(zoneText[0].getText())
                                        );
                            } else{
                                check = false;
                            }
                        }
                        if(!check){
                            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                            messageBox.setMessage("Zone Fields must be Integer type.");
                            messageBox.open();
                        }
                    } else {
                        where = where + ";(name::int >= 0)";
                    }


                    if(yearButton.getSelection()){
                        if(yearCombo.getText().equals(CONDITIONS[5])){
                            if(isStringInt(yearText[0].getText())&&isStringInt(yearText[1].getText())) {
                                where = where + ";(year" + CONDITIONS[5] + yearText[0].getText() + " AND " + yearText[1].getText() + ")";
                            } else {
                                check = false;
                            }
                        }
                        else{
                            if(isStringInt(yearText[0].getText())) {
                                where = where + ";(year" + yearCombo.getText() + yearText[0].getText() + ")";
                            } else {
                                check = false;
                            }
                        }
                        if(!check){
                            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                            messageBox.setMessage("Year Fields must be Integer type.");
                            messageBox.open();
                        }
                    } else {
                        where = where + ";(year >= 0)";
                    }

                    if(dayButton.getSelection()){
                        if(dayCombo.getText().equals(CONDITIONS[5])){
                            if(isStringInt(dayText[0].getText())&&isStringInt(dayText[1].getText())) {
                                where = where + ";(day" + CONDITIONS[5] + dayText[0].getText() + " AND " + dayText[1].getText() + ")";
                            } else {
                                check = false;
                            }
                        }
                        else{
                            if(isStringInt(dayText[0].getText())) {
                                where = where + ";(day" + dayCombo.getText() + dayText[0].getText() + ")";
                            } else {
                                check = false;
                            }
                        }
                        if(!check){
                            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                            messageBox.setMessage("Day Fields must be Integer type.");
                            messageBox.open();
                        }
                    } else {
                        where = where + ";(day >= 0)";
                    }

                    if(envButton.getSelection()){
                        String[] envIndices = envList1.getItems();
                        if(envIndices.length>0){
                            where = where + ";(index IN (";
                            for(int i=0;i<envIndices.length;i++){
                                where = where + EnvironmentalIndex.valueOf(envIndices[i]).ordinal();
                                where = where + ",";
                            }
                            where = where.substring(0, where.length()-1);
                            where = where + "))";
                        }
                    }

                    query = new SQL(field.split(","), projCombo.getText(), where.split(";"));

                    //                    }

                    if(check){
                        try{
                            //QueryResult result = new QueryResult(shell, field.split(","), query.getQuery());
                            QueryProgress bar = new QueryProgress(shell, field, query);
                            bar.open();
                        } catch(SQLException e){
                            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                            if (e.getMessage() != null) {
                                messageBox.setMessage(e.getMessage());
                            }
                            messageBox.open();
                        }catch(ConfigReadException e){
                            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                            messageBox.setMessage(e.getMessage());
                            messageBox.open();
                        }catch(Exception e){
                            MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                            e.printStackTrace();
                            //messageBox.setMessage(e.getMessage());
                            messageBox.open();
                        }finally{
                        }
                    }
                }
                else{
                    MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                    messageBox.setMessage("Please select any fields such 'count', 'sum', 'mean', 'stdev', 'enviromental indices'.");
                    messageBox.open();
                }
            }
        });

        // Composite for Main
        Composite settingsComposite = new Composite(shell, SWT.BORDER);
        {
            FormData data = new FormData();
            data.top = new FormAttachment(0,0);
            data.right = new FormAttachment(100,0);
            data.left = new FormAttachment(0,0);
            data.bottom = new FormAttachment(controlsComposite, 0, SWT.TOP);
            settingsComposite.setLayoutData(data);
        }
        {
            GridLayout gridLayout = new GridLayout(1, false);
            gridLayout.marginWidth = 15;
            gridLayout.marginHeight = 15;
            gridLayout.verticalSpacing = 10;
            settingsComposite.setLayout(gridLayout);
        }

        // Composite for Projection
        Composite projComposite = new Composite(settingsComposite, SWT.BORDER);
        {
            GridLayout gridLayout = new GridLayout(2, false);
            projComposite.setLayout(gridLayout);
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            projComposite.setLayoutData(data);
        }
        Label projLabel = new Label(projComposite, SWT.NONE);
        projLabel.setText(" Project ");
        projCombo = new Combo(projComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        {
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            projCombo.setLayoutData(data);
            try {
                Connection conn = DriverManager.getConnection(
                        Config.getInstance().getDatabaseHost(),
                        Config.getInstance().getDatabaseUsername(),
                        Config.getInstance().getDatabasePassword()
                        );

                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("Select * FROM pg_namespace");

                java.util.List<String> list = new ArrayList<String>();
                while(rs.next()){
                    System.out.println(rs.getString(1));
                    if (rs.getString(1).length() > 8 && rs.getString(1).substring(0, 8).equals("project_")) {
                        list.add(rs.getString(1).substring(8));
                    }
                }

                if (list.size() <= 0) {
                    throw new ConfigReadException("There are no projects.");
                }
                else {
                    projCombo.setItems(list.toArray(new String[0]));
                    projCombo.setText(list.get(0));
                }

            }catch(ConfigReadException e){
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                messageBox.setMessage(e.getMessage());
                messageBox.open();
            }catch(Exception e){
                MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
                messageBox.setMessage(e.getMessage());
                messageBox.open();
            }
        }

        // Composite for Query
        Composite query = new Composite(settingsComposite, SWT.BORDER);
        {
            GridLayout gridLayout = new GridLayout(5, false);
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            query.setLayout(gridLayout);
            query.setLayoutData(data);
        }

        // Fields
        Group fields = new Group(query, SWT.NONE);
        {

            GridLayout gridLayout = new GridLayout(4, true);
            fields.setLayout(gridLayout);
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            data.horizontalSpan = 5;
            fields.setLayoutData(data);
            fields.setText("Fields");
        }
        fieldsButton = new Button[4];
        {
            fieldsButton[0] = new Button(fields, SWT.CHECK);
            fieldsButton[0].setText("count");
            fieldsButton[0].setSelection(true);
            fieldsButton[0].addSelectionListener(new SelectionListener(){
                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    if(fieldsButton[0].getSelection()) {
                        field = field + ",count";
                    } else {
                        field = field.replace(",count", "");
                    }
                }
            });
            fieldsButton[1] = new Button(fields, SWT.CHECK);
            fieldsButton[1].setText("sum");
            fieldsButton[1].setSelection(true);
            fieldsButton[1].addSelectionListener(new SelectionListener(){
                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    if(fieldsButton[1].getSelection()) {
                        field = field + ",sum";
                    } else {
                        field = field.replace(",sum", "");
                    }
                }
            });
            fieldsButton[2] = new Button(fields, SWT.CHECK);
            fieldsButton[2].setText("mean");
            fieldsButton[2].setSelection(true);
            fieldsButton[2].addSelectionListener(new SelectionListener(){
                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    if(fieldsButton[2].getSelection()) {
                        field = field + ",mean";
                    } else {
                        field = field.replace(",mean", "");
                    }
                }
            });
            fieldsButton[3] = new Button(fields, SWT.CHECK);
            fieldsButton[3].setText("stdev");
            fieldsButton[3].setSelection(true);
            fieldsButton[3].addSelectionListener(new SelectionListener(){
                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    if(fieldsButton[3].getSelection()) {
                        field = field + ",stdev";
                    } else {
                        field = field.replace(",stdev", "");
                    }
                }
            });
        }

        // ZONE
        zoneButton = new Button(query, SWT.CHECK);
        zoneButton.setText("ZONE ");
        zoneButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(zoneButton.getSelection()){
                    zoneCombo.setEnabled(zoneButton.getSelection());
                    zoneText[0].setEnabled(zoneButton.getSelection());
                }
                else{
                    zoneCombo.setEnabled(false);
                    zoneText[0].setEnabled(false);
                    zoneText[1].setEnabled(false);
                    zoneLabel.setEnabled(false);
                }
            }
        });
        zoneCombo = new Combo(query, SWT.DROP_DOWN | SWT.READ_ONLY);
        zoneCombo.setItems(CONDITIONS);
        zoneCombo.setText(CONDITIONS[0]);
        zoneCombo.setEnabled(false);
        zoneCombo.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(zoneCombo.getText().equals(CONDITIONS[5])){
                    zoneLabel.setEnabled(true);
                    zoneText[1].setEnabled(true);
                }
                else{
                    zoneLabel.setEnabled(false);
                    zoneText[1].setEnabled(false);
                }
            }
        });
        zoneText = new Text[2];
        zoneText[0] = new Text(query, SWT.BORDER);
        zoneText[0].setEnabled(false);
        zoneLabel = new Label(query, SWT.NONE);
        zoneLabel.setText(" AND ");
        zoneLabel.setEnabled(false);
        zoneText[1] = new Text(query, SWT.BORDER);
        zoneText[1].setEnabled(false);
        // YEAR
        yearButton = new Button(query, SWT.CHECK);
        yearButton.setText("YEAR ");
        yearButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(yearButton.getSelection()){
                    yearCombo.setEnabled(yearButton.getSelection());
                    yearText[0].setEnabled(yearButton.getSelection());
                }
                else{
                    yearCombo.setEnabled(false);
                    yearText[0].setEnabled(false);
                    yearText[1].setEnabled(false);
                    yearLabel.setEnabled(false);
                }
            }
        });
        yearCombo = new Combo(query, SWT.DROP_DOWN | SWT.READ_ONLY);
        yearCombo.setItems(CONDITIONS);
        yearCombo.setText(CONDITIONS[0]);
        yearCombo.setEnabled(false);
        yearCombo.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(yearCombo.getText().equals(CONDITIONS[5])){
                    yearLabel.setEnabled(true);
                    yearText[1].setEnabled(true);
                }
                else{
                    yearLabel.setEnabled(false);
                    yearText[1].setEnabled(false);
                }
            }
        });
        yearText = new Text[2];
        yearText[0] = new Text(query, SWT.BORDER);
        yearText[0].setEnabled(false);
        yearLabel = new Label(query, SWT.NONE);
        yearLabel.setText(" AND ");
        yearLabel.setEnabled(false);
        yearText[1] = new Text(query, SWT.BORDER);
        yearText[1].setEnabled(false);
        // DAY
        dayButton = new Button(query, SWT.CHECK);
        dayButton.setText("DAY ");
        dayButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(dayButton.getSelection()){
                    dayCombo.setEnabled(dayButton.getSelection());
                    dayText[0].setEnabled(dayButton.getSelection());
                }
                else{
                    dayCombo.setEnabled(false);
                    dayText[0].setEnabled(false);
                    dayText[1].setEnabled(false);
                    dayLabel.setEnabled(false);
                }
            }
        });
        dayCombo = new Combo(query, SWT.DROP_DOWN | SWT.READ_ONLY);
        dayCombo.setItems(CONDITIONS);
        dayCombo.setText(CONDITIONS[0]);
        dayCombo.setEnabled(false);
        dayCombo.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(dayCombo.getText().equals(CONDITIONS[5])){
                    dayLabel.setEnabled(true);
                    dayText[1].setEnabled(true);
                }
                else{
                    dayLabel.setEnabled(false);
                    dayText[1].setEnabled(false);
                }
            }
        });
        dayText = new Text[2];
        dayText[0] = new Text(query, SWT.BORDER);
        dayText[0].setEnabled(false);
        dayLabel = new Label(query, SWT.NONE);
        dayLabel.setText(" AND ");
        dayLabel.setEnabled(false);
        dayText[1] = new Text(query, SWT.BORDER);
        dayText[1].setEnabled(false);

        // Environmental Index
        envGroup = new Group(query, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout();
            GridData data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            data.horizontalSpan = 5;
            envGroup.setLayout(gridLayout);
            envGroup.setLayoutData(data);
            envGroup.setText("Environmental Index");
        }
        envButton = new Button(envGroup, SWT.CHECK);
        envButton.setText("Enable");
        envButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if(envButton.getSelection()) {
                    in.setEnabled(true);
                    out.setEnabled(true);
                    field = field + ",index";
                } else {
                    in.setEnabled(false);
                    out.setEnabled(false);
                    field = field.replace(",index", "");
                }
            }
        });
        Composite env = new Composite(envGroup, SWT.NONE);
        {
            GridLayout gridLayout = new GridLayout(5, false);
            gridLayout.makeColumnsEqualWidth = true;
            gridLayout.horizontalSpacing = 30;
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            env.setLayout(gridLayout);
            env.setLayoutData(data);
        }
        Label include = new Label(env, SWT.NONE);
        include.setText("Include");
        /* Label empty = */ new Label(env, SWT.NONE);
        /* empty = */ new Label(env, SWT.NONE);
        Label exclude = new Label(env, SWT.NONE);
        exclude.setText("Exclude");
        /* empty = */ new Label(env, SWT.NONE);
        envList1 = new List(env, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        {
            GridData data = new GridData(GridData.FILL_BOTH);
            data.horizontalSpan = 2;
            data.verticalSpan = 2;
            envList1.setLayoutData(data);
        }
        in = new Button(env, SWT.PUSH);
        {
            GridData data = new GridData(GridData.FILL_BOTH);
            in.setLayoutData(data);
            in.setText(" < ");
            in.addSelectionListener(new SelectionListener(){
                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    String[] temp = envList2.getSelection().clone();
                    for(int i=0;i<temp.length;i++){
                        envList1.add(temp[i]);
                        envList2.remove(temp[i]);
                    }

                    if (temp.length > 0) {
                        envList2.select(0);
                    }
                }
            });
            in.setEnabled(false);
        }
        envList2 = new List(env, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        {
            GridData data = new GridData(GridData.FILL_BOTH);
            envList2.setLayoutData(data);
            data.horizontalSpan = 2;
            data.verticalSpan = 2;
            EnvironmentalIndex[] indices = EnvironmentalIndex.values();
            for(int i=0 ;i<indices.length ;i++){
                envList2.add(indices[i].name());
            }
        }
        out = new Button(env, SWT.PUSH);
        {
            GridData data = new GridData(GridData.FILL_BOTH);
            out.setLayoutData(data);
            out.setText(" >  ");
            out.addSelectionListener(new SelectionListener(){
                @Override
                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
                @Override
                public void widgetSelected(SelectionEvent arg0) {
                    String[] temp = envList1.getSelection().clone();
                    for(int i=0;i<temp.length;i++){
                        envList2.add(temp[i]);
                        envList1.remove(temp[i]);
                    }

                    if (temp.length > 0) {
                        envList1.select(0);
                    }
                }
            });
            out.setEnabled(false);
        }

        // Button for viewing SQL
        sql = new Button(settingsComposite, SWT.PUSH);
        sql.setText("SQL View");
        {
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            sql.setLayoutData(data);
        }
        sql.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                SQL temp;
                where = "";
                if(zoneButton.getSelection()){
                    if(zoneCombo.getText().equals(CONDITIONS[5])){
                        if(isStringInt(zoneText[0].getText())&&isStringInt(zoneText[1].getText())) {
                            //where = where + ";(\"zoneID\"" + CONDITIONS[5] + zoneText[0].getText() + " AND " + zoneText[1].getText() + ")";
                            where += String.format(
                                    ";(name::int %s %d AND %d)",
                                    zoneCombo.getText(),
                                    Integer.parseInt(zoneText[0].getText()),
                                    Integer.parseInt(zoneText[1].getText())
                                    );
                        }
                    }
                    else{
                        if(isStringInt(zoneText[0].getText())) {
                            //where = where + ";(\"zoneID\"" + zoneCombo.getText() + zoneText[0].getText() + ")";
                            where += String.format(
                                    ";(name::int %s %d)",
                                    zoneCombo.getText(),
                                    Integer.parseInt(zoneText[0].getText())
                                    );
                        }
                    }
                } else {
                    where = where + ";(name::int >= 0)";
                }
                if(yearButton.getSelection()){
                    if(yearCombo.getText().equals(CONDITIONS[5])) {
                        where = where + ";(year" + CONDITIONS[5] + yearText[0].getText() + " AND " + yearText[1].getText() + ")";
                    } else {
                        where = where + ";(year" + yearCombo.getText() + yearText[0].getText() + ")";
                    }
                } else {
                    where = where + ";(year >= 0)";
                }
                if(dayButton.getSelection()){
                    if(dayCombo.getText().equals(CONDITIONS[5])) {
                        where = where + ";(day" + CONDITIONS[5] + dayText[0].getText() + " AND " + dayText[1].getText() + ")";
                    } else {
                        where = where + ";(day" + dayCombo.getText() + dayText[0].getText() + ")";
                    }
                } else {
                    where = where + ";(day >= 0)";
                }
                if(envButton.getSelection()){
                    String[] envIndices = envList1.getItems();
                    if(envIndices.length>0){
                        where = where + ";(index IN (";
                        for(int i=0;i<envIndices.length;i++){
                            where = where + EnvironmentalIndex.valueOf(envIndices[i]).ordinal();
                            where = where + ",";
                        }
                        where = where.substring(0, where.length()-1);
                        where = where + "))";
                        System.out.println("ENVTEST: " + where);
                    }
                }
                temp = new SQL(field.split(","), projCombo.getText(), where.split(";"));
                //                }
                SQLView sqlView = new SQLView(shell, temp.getQuery());
                sqlView.open();
            }
        });
        shell.pack();
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    public void dispose() {
        shell.dispose();
    }

    public void addDisposeListener(DisposeListener listener) {
        shell.addDisposeListener(listener);
    }

    // This method checks whether a String is a integer number
    private boolean isStringInt (String s) {
        try{
            Integer.parseInt(s);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

}
