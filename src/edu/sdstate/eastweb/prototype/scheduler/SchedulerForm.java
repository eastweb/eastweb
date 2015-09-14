package edu.sdstate.eastweb.prototype.scheduler;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.database.DatabaseManagerOld;
import edu.sdstate.eastweb.prototype.database.Query;
import edu.sdstate.eastweb.prototype.projectgui.AddZonalSummariesWizard;
import edu.sdstate.eastweb.prototype.projectgui.NewProjectWizard;
import edu.sdstate.eastweb.prototype.scheduler.SchedulerEvents.SchedulerEventListener;
import edu.sdstate.eastweb.prototype.scheduler.LocalScheduler;

public class SchedulerForm {

    private static final int TASK_PROGRESS_COLUMN_INDEX = 1;
    private static final int TASK_GROUP_PROGRESS_TEXT_COLUMN_INDEX = 1;
    private static final int TASK_GROUP_PROGRESS_BAR_COLUMN_INDEX = 2;

    private final class TaskEntry {
        private TableItem mItem;
        private ProgressBar mProgressBar;
        private TableEditor mEditor;

        public TaskEntry(String name, boolean reportsProgress) {
            mItem = new TableItem(jobsTbl, SWT.NONE);
            mItem.setText(name);

            if (reportsProgress) {
                mProgressBar = new ProgressBar(jobsTbl, SWT.NONE);
            } else {
                mProgressBar = new ProgressBar(jobsTbl, SWT.INDETERMINATE);
            }

            mEditor = new TableEditor(jobsTbl);
            mEditor.grabHorizontal = mEditor.grabVertical = true;
            mEditor.setEditor(mProgressBar, mItem, TASK_PROGRESS_COLUMN_INDEX);

            updateLayouts();
        }

        public void update(int progress, int total) {
            mProgressBar.setMaximum(total);
            mProgressBar.setSelection(progress);
        }

        public void dispose() {
            if (mEditor != null) {
                mEditor.dispose();
                mEditor = null;
            }

            if (mProgressBar != null) {
                mProgressBar.dispose();
                mProgressBar = null;
            }

            if (mItem != null) {
                mItem.dispose();
                mItem = null;

                updateLayouts();
            }
        }

        public void updateLayout() {
            if (mEditor != null) {
                mEditor.layout();
            }
        }
    }

    private enum SchedulerState {
        NoScheduler,
        Starting,
        Running,
        Stopping,
    }

    private SchedulerState mSchedulerState = SchedulerState.NoScheduler;
    private boolean mIsShuttingDown = false;
    private boolean mRestartWhenStarted = false;
    private Scheduler mScheduler;
    private Thread mThread;
    private SchedulerEventListener mSchedulerEventListener;

    private Shell shell;
    private Map<String, TableItem> taskGroups;
    private final Map<String, TaskEntry> mTasks = new HashMap<String, TaskEntry>();

    private Menu menuBar;
    private MenuItem fileMenuHeader;
    private MenuItem newProjectItem;
    private MenuItem editProjectItem;
    private MenuItem setActiveProjectsItem;
    private Menu fileMenu;
    private MenuItem restartSchedulerItem;
    private MenuItem exitItem;

    private Label progressLbl;
    private Table groupTbl;
    private Label jobsLbl;
    private Table jobsTbl;
    private Composite errorsCmp;
    private Label errorsLbl;
    private Table errorsTbl;
    private Label statusLbl;

    public SchedulerForm(Display display) {
        taskGroups = new Hashtable<String, TableItem>();

        shell = new Shell(display);
        shell.setText("EASTWeb");
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(new Image(display, "./icon.png"));


        setupWidgets();
        setupListeners();

        startScheduler();
    }

    private void setupWidgets() {
        // Setup the menu bar
        menuBar = new Menu(shell, SWT.BAR);

        fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
        fileMenuHeader.setText("&Scheduler");

        fileMenu = new Menu(shell, SWT.DROP_DOWN);
        fileMenuHeader.setMenu(fileMenu);

        restartSchedulerItem = new MenuItem(fileMenu, SWT.PUSH);
        restartSchedulerItem.setText("&Restart scheduler");

        new MenuItem(fileMenu, SWT.SEPARATOR);

        exitItem = new MenuItem(fileMenu, SWT.PUSH);
        exitItem.setText("&Exit");

        final MenuItem viewMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
        viewMenuHeader.setText("&Projects");

        final Menu viewMenu = new Menu(shell, SWT.DROP_DOWN);
        viewMenuHeader.setMenu(viewMenu);

        final MenuItem queryItem = new MenuItem(viewMenu, SWT.PUSH);
        queryItem.setText("&Query project");
        queryItem.addSelectionListener(new QueryItemSelectionListener());

        //new MenuItem(viewMenu, SWT.SEPARATOR);

        newProjectItem = new MenuItem(viewMenu, SWT.PUSH);
        newProjectItem.setText("&Create project");

        editProjectItem = new MenuItem(viewMenu, SWT.PUSH);
        editProjectItem.setText("&Edit project");

        /*
        final MenuItem projectsItem = new MenuItem(viewMenu, SWT.PUSH);
        projectsItem.setText("&Projects window");
        projectsItem.addSelectionListener(new ProjectsItemSelectionListener());
         */

        setActiveProjectsItem = new MenuItem(viewMenu, SWT.PUSH);
        setActiveProjectsItem.setText("&Set active projects");

        shell.setMenuBar(menuBar);


        // Setup the tables composite that contains both tables and makes them equally sized
        Composite tablesCmp = new Composite(shell, SWT.NONE);
        tablesCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tablesCmp.setLayout(new FillLayout(SWT.VERTICAL));

        // Setup "Overall progress" table
        Composite progressCmp = new Composite(tablesCmp, SWT.NONE);
        progressCmp.setLayout(new GridLayout(1, false));

        progressLbl = new Label(progressCmp, SWT.NONE);
        progressLbl.setText("Overall progress");
        progressLbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        groupTbl = new Table(progressCmp, SWT.BORDER);
        groupTbl.setHeaderVisible(true);
        groupTbl.setLinesVisible(true);
        {
            final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.heightHint = 250;
            groupTbl.setLayoutData(gridData);
        }

        TableColumn groupCol = new TableColumn(groupTbl, SWT.NONE);
        groupCol.setText("Category");
        groupCol.pack();
        groupCol.setWidth(350);

        TableColumn progressTextCol = new TableColumn(groupTbl, SWT.NONE);
        progressTextCol.setText("Progress");
        progressTextCol.pack();
        progressTextCol.setWidth(100);

        TableColumn progressBarCol = new TableColumn(groupTbl, SWT.NONE);
        progressBarCol.setText("");
        progressBarCol.pack();
        progressBarCol.setWidth(100);

        // Setup "Active tasks" table
        Composite jobsCmp = new Composite(tablesCmp, SWT.NONE);
        jobsCmp.setLayout(new GridLayout(1, false));

        jobsLbl = new Label(jobsCmp, SWT.NONE);
        jobsLbl.setText("Active tasks");
        jobsLbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        jobsTbl = new Table(jobsCmp, SWT.BORDER);
        jobsTbl.setHeaderVisible(true);
        jobsTbl.setLinesVisible(true);
        {
            final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.heightHint = 250;
            jobsTbl.setLayoutData(gridData);
        }

        TableColumn jobNameCol = new TableColumn(jobsTbl, SWT.NONE);
        jobNameCol.setText("Task");
        jobNameCol.setWidth(500);

        //TableColumn progressCol = new TableColumn(jobsTbl, SWT.NONE);
        //progressCol.setText("Progress");
        //progressCol.setWidth(100);

        // Setup the errors group
        errorsCmp = new Composite(shell, SWT.NONE);
        errorsCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        errorsCmp.setLayout(new GridLayout(1, false));

        errorsLbl = new Label(errorsCmp, SWT.NONE);
        errorsLbl.setText("Processing errors");
        errorsLbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        errorsTbl = new Table(errorsCmp, SWT.BORDER);
        errorsTbl.setHeaderVisible(true);
        errorsTbl.setLinesVisible(true);
        {
            final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            gridData.heightHint = 100;
            errorsTbl.setLayoutData(gridData);
        }

        TableColumn taskCol = new TableColumn(errorsTbl, SWT.NONE);
        taskCol.setText("Task");
        taskCol.pack();
        taskCol.setWidth(350);

        TableColumn causeCol = new TableColumn(errorsTbl, SWT.NONE);
        causeCol.setText("Cause");
        causeCol.pack();
        causeCol.setWidth(200);

        // Setup the scheduler status label
        statusLbl = new Label(shell, SWT.CENTER);
        statusLbl.setText("Initializing...");
        statusLbl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        shell.pack();
        //shell.setMaximized(true);
    }

    private void setupListeners() {
        newProjectItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                // Open a NewProjectWizard and let the user create a project
                NewProjectWizard wizard = new NewProjectWizard();

                WizardDialog dialog = new WizardDialog(shell, wizard) {
                    @Override
                    protected Point getInitialLocation(Point initialSize) {
                        return new Point(
                                shell.getLocation().x + (shell.getSize().x - initialSize.x)/2,
                                shell.getLocation().y + (shell.getSize().y - initialSize.y)/2
                        );
                    }
                };
                dialog.setBlockOnOpen(true);
                int status = dialog.open();

                if (status == WizardDialog.OK) {
                    ProjectInfo project = wizard.getProjectInfo();
                    try {
                        // [Re]create the database schema
                        Connection conn = null;
                        try {
                            conn = DriverManager.getConnection(
                                    Config.getInstance().getDatabaseHost(),
                                    Config.getInstance().getDatabaseUsername(),
                                    Config.getInstance().getDatabasePassword()
                            );
                            new DatabaseManagerOld(conn, project).recreateSchema();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            final MessageBox mb = new MessageBox(shell);
                            mb.setMessage("Failed to create database schema: " +
                                    e2.getMessage());
                            mb.open();
                            throw e2;
                        } finally {
                            if (conn != null) {
                                try {
                                    conn.close();
                                } catch (SQLException e1) {
                                }
                            }
                        }

                        // [Re]create the project settings directory
                        final File settingsDir;
                        try {
                            settingsDir = DirectoryLayout.getSettingsDirectory(project);
                            if (settingsDir.exists()) {
                                FileUtils.forceDelete(settingsDir);
                            }
                            FileUtils.forceMkdir(settingsDir);
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            final MessageBox mb = new MessageBox(shell);
                            mb.setMessage("Failed to create project settings directory: " +
                                    e2.getMessage());
                            mb.open();
                            throw e2;
                        }

                        // Copy elevation data
                        if (project.shouldCalculateETa()) {
                            File src = null;
                            File dest = null;
                            try {
                                src = new File(project.getElevation());
                                dest = new File(new File(settingsDir, "elevation"), src.getName());
                                FileUtils.copyFile(src, dest);
                            } catch (IOException e2) {
                                e2.printStackTrace();
                                final MessageBox mb = new MessageBox(shell);
                                mb.setMessage("Failed to copy elevation data: " +
                                        e2.getMessage());
                                mb.open();
                                if (dest != null) {
                                    FileUtils.deleteQuietly(dest);
                                }
                                throw e2;
                            }
                        }

                        // Copy water mask data
                        try {
                            final File src = new File(project.getWatermask());
                            final File dest = new File(new File(settingsDir, "watermask"), src.getName());
                            FileUtils.copyFile(src, dest);
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            final MessageBox mb = new MessageBox(shell);
                            mb.setMessage("Failed to copy water mask data: " +
                                    e2.getMessage());
                            mb.open();
                            throw e2;
                        }

                        // Copy shapefile data
                        try {
                            final File shapefileDir = new File(settingsDir, "shapefiles");
                            final List<String> shapeFiles = project.getShapeFiles();
                            for (String srcShapeFile : shapeFiles) {
                                final String prefix = new File(srcShapeFile).getName().replaceFirst("shp$", "");

                                // Name and create the destination directory
                                final File destDir = new File(shapefileDir, prefix);
                                FileUtils.forceMkdir(destDir);

                                // Copy all files with the same name as the shapefile (not counting ext)
                                for (File file : new File(srcShapeFile).getParentFile().listFiles()) {
                                    if (file.getName().startsWith(prefix)) {
                                        FileUtils.copyFileToDirectory(file, destDir);
                                    }
                                }
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            final MessageBox mb = new MessageBox(shell);
                            mb.setMessage("Failed to copy shapefile data: " +
                                    e2.getMessage());
                            mb.open();
                            throw e2;
                        }

                        // Attempt to save the project
                        try {
                            // Adjust watermask, elevation, and zonal summaries to point to the copied data
                            final String watermask = new File("watermask",
                                    new File(project.getWatermask()).getName()).toString();
                            final String elevation = new File("elevation",
                                    new File(project.getElevation()).getName()).toString();

                            final ZonalSummary[] zonalSummaries = project.getZonalSummaries();
                            for (int zonalSummaryId = 0; zonalSummaryId < zonalSummaries.length; ++zonalSummaryId)
                            {
                                final ZonalSummary zone = zonalSummaries[zonalSummaryId];
                                String shapeFile = new File(zone.getShapeFile()).getName();
                                zonalSummaries[zonalSummaryId] = new ZonalSummary(
                                        zone.getName(),
                                        new File(
                                                "shapefiles",
                                                new File(shapeFile.substring(0, shapeFile.indexOf('.')), shapeFile).toString()
                                        ).toString(),
                                        zone.getField()
                                );
                            }


                            Config.getInstance().saveProject(new ProjectInfo(
                                    project.getName(),
                                    project.getStartDate(),
                                    watermask,
                                    elevation,
                                    project.getMinLst(),
                                    project.getMaxLst(),
                                    project.getProjection(),
                                    project.getModisTiles(),
                                    zonalSummaries,
                                    project.shouldCalculateETa()
                            ));
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            final MessageBox mb = new MessageBox(shell);
                            mb.setMessage("Failed to save project: " + e2.getMessage());
                            mb.open();
                            throw e2;
                        }

                        restartScheduler();
                    } catch (Exception e) { // Clean up if it failed
                        try {
                            FileUtils.deleteQuietly(DirectoryLayout.getSettingsDirectory(project).getParentFile());
                        } catch (ConfigReadException e1) {
                            e1.printStackTrace();
                            final MessageBox mb = new MessageBox(shell);
                            mb.setMessage("Project creation failure cleanup failed: " + e1.getMessage());
                            mb.open();
                        }
                    }
                }
            }
        });


        // FIXME: in serious need of refactoring!
        editProjectItem.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(Event event) {
                try {
                    if (Config.getInstance().getProjectNames().length > 0) {
                        AddZonalSummariesWizard wizard = new AddZonalSummariesWizard();

                        WizardDialog dialog = new WizardDialog(shell, wizard) {
                            @Override
                            protected Point getInitialLocation(Point initialSize) {
                                return new Point(
                                        shell.getLocation().x + (shell.getSize().x - initialSize.x)/2,
                                        shell.getLocation().y + (shell.getSize().y - initialSize.y)/2
                                );
                            }
                        };
                        dialog.setBlockOnOpen(true);
                        int status = dialog.open();

                        if (status == WizardDialog.OK) {
                            ProjectInfo project = wizard.getProjectInfo();

                            ZonalSummary[] summaries = project.getZonalSummaries();
                            for (int i=0; i<summaries.length; i++) {
                                ZonalSummary summary = summaries[i];

                                String shapeFile = new File(summary.getShapeFile()).getName();
                                String shapeFileRel = new File(
                                        new File(
                                                "shapefiles",
                                                shapeFile.substring(0, shapeFile.indexOf('.'))
                                        ).toString(),
                                        shapeFile
                                ).toString();
                                String shapeFileAbs = new File(
                                        DirectoryLayout.getSettingsDirectory(project),
                                        shapeFileRel
                                ).toString();

                                if (!(new File(shapeFileAbs).exists())) {
                                    // Copy the shapefile
                                    File settingsDir = DirectoryLayout.getSettingsDirectory(project);

                                    final File shapefileDir = new File(settingsDir, "shapefiles");
                                    final String prefix = new File(summary.getShapeFile()).getName().replaceFirst("shp$", "");

                                    // Name and create the destination directory
                                    final File destDir = new File(shapefileDir, prefix);
                                    FileUtils.forceMkdir(destDir);

                                    // Copy all files with the same name as the shapefile (not counting ext)
                                    for (File file : new File(summary.getShapeFile()).getParentFile().listFiles()) {
                                        if (file.getName().startsWith(prefix)) {
                                            FileUtils.copyFileToDirectory(file, destDir);
                                        }
                                    }
                                }

                                // Change what the summary points to
                                final ZonalSummary zone = summaries[i];
                                summaries[i] = new ZonalSummary(
                                        zone.getName(),
                                        shapeFileRel.toString(),
                                        zone.getField()
                                );
                            }

                            project.setZonalSummaries(summaries);

                            Config.getInstance().saveProject(project);

                            restartScheduler();
                        }
                    } else {
                        final MessageBox mb = new MessageBox(shell);
                        mb.setMessage("At least one project needs to exist to use this feature.");
                        mb.open();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    final MessageBox mb = new MessageBox(shell);
                    mb.setMessage("Failed to copy shapefile data: " +
                            e.getMessage());
                    mb.open();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    final MessageBox mb = new MessageBox(shell);
                    mb.setMessage("Editing zonal summaries failed: " +
                            e.getMessage());
                    mb.open();
                    return;
                }
            }

        });

        restartSchedulerItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                restartScheduler();
            }
        });

        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shutdownScheduler();

                // Delay closing the shell until shutdown is complete
                if (mSchedulerState != SchedulerState.NoScheduler) {
                    e.doit = false;
                }
            }
        });

        setActiveProjectsItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ActiveProjectsForm form = new ActiveProjectsForm(shell);
                int result = form.open();

                if (result == SWT.OK) {
                    restartScheduler();
                }
            }
        });

        mSchedulerEventListener = new SchedulerEventListener() {
            @Override
            public void newTask(final String taskName, final boolean reportsProgress) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        final TaskEntry entry = new TaskEntry(taskName, reportsProgress);
                        mTasks.put(taskName, entry);
                    }
                });
            }

            @Override
            public void taskUpdated(final String taskName, final int progress, final int total) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        mTasks.get(taskName).update(progress, total);
                    }
                });
            }

            @Override
            public void taskCompleted(final String taskName) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        final TaskEntry entry = mTasks.get(taskName);
                        mTasks.remove(entry);
                        entry.dispose();
                    }
                });
            }

            @Override
            public void taskFailed(final String taskName, final Throwable cause) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        final TableItem item = new TableItem(errorsTbl, SWT.NONE);
                        item.setText(new String[] {
                                taskName,
                                cause.getMessage()
                        });

                        final TaskEntry entry = mTasks.get(taskName);
                        mTasks.remove(entry);
                        entry.dispose();
                    }
                });
            }

            @Override
            public void taskGroupUpdated(final String taskGroupName, final int progress,
                    final int total) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        SchedulerForm.this.taskGroupUpdated(taskGroupName, progress, total);
                    }
                });
            }
        };

        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                shutdownScheduler();

                // Delay closing the shell until shutdown is complete
                if (mSchedulerState != SchedulerState.NoScheduler) {
                    e.doit = false;
                }
            }
        });

        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                shutdownScheduler();
            }
        });

        registerRestartCallback();
    }

    /**
     * Sets a timer to restart the scheduler in one day. Calls itself after issuing the restart.
     */
    private void registerRestartCallback() {
        final int ONE_DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;
        shell.getDisplay().timerExec(ONE_DAY_IN_MILLISECONDS, new Runnable() {
            @Override
            public void run() {
                restartScheduler();
                registerRestartCallback();
            }
        });
    }

    private void updateLayouts() {
        for (TaskEntry entry : mTasks.values()) {
            entry.updateLayout();
        }
    }

    private void taskGroupUpdated(String taskGroupName, int progress, int total) {
        final TableItem item = taskGroups.get(taskGroupName);
        item.setText(TASK_GROUP_PROGRESS_TEXT_COLUMN_INDEX,
                String.format("%d/%d", progress, total));

        final ProgressBar progressBar = (ProgressBar)item.getData();
        progressBar.setMaximum(total);
        progressBar.setSelection(progress);
    }

    public void open() {
        shell.open();
    }

    public boolean isDisposed() {
        return shell.isDisposed();
    }

    private void startScheduler() {
        switch (mSchedulerState) {
        case NoScheduler:
            // Start it up
            statusLbl.setText("Starting the task scheduler...");
            mSchedulerState = SchedulerState.Starting;
            new Thread(new StartSchedulerRunnable()).start();
            break;

        case Starting:
        case Running:
        case Stopping:
            // No action necessary -- the stopped handler restarts it by default
            break;
        }
    }

    private void restartScheduler() {
        switch (mSchedulerState) {
        case NoScheduler:
            // Start it up
            statusLbl.setText("Starting the task scheduler...");
            mSchedulerState = SchedulerState.Starting;
            new Thread(new StartSchedulerRunnable()).start();
            break;

        case Starting:
            // Tell the started handler to stop right away
            statusLbl.setText("Waiting for the task scheduler to start so it can be restarted...");
            mRestartWhenStarted = true;
            break;

        case Running:
            // Stop it -- the stopped handler restarts it by default
            statusLbl.setText("Waiting for the task scheduler to stop so it can be restarted...");
            mSchedulerState = SchedulerState.Stopping;
            new Thread(new StopSchedulerRunnable()).start();
            break;

        case Stopping:
            // No action necessary -- the stopped handler restarts it by default
            break;
        }
    }

    private void shutdownScheduler() {
        mIsShuttingDown = true;

        switch (mSchedulerState) {
        case NoScheduler:
            // No action necessary -- it's already stopped
            break;

        case Starting:
            // No action necessary -- the started handler will stop it when the flag is set
            statusLbl.setText("Waiting for the task scheduler to start so we can shut down...");
            break;

        case Running:
            // Stop it -- the stopped handler will not restart when the flag is set
            statusLbl.setText("Waiting for the task scheduler to stop so we can shut down...");
            mSchedulerState = SchedulerState.Stopping;
            new Thread(new StopSchedulerRunnable()).start();
            break;

        case Stopping:
            // No action necessary -- the stopped handler will not restart when the flag is set
            statusLbl.setText("Waiting for the task scheduler to stop so we can shut down...");
            break;
        }
    }

    /**
     * Runs on a background thread to start the scheduler.
     */
    private final class StartSchedulerRunnable implements Runnable {
        @Override
        public void run() {
            // Load all of the projects
            Scheduler scheduler = null;
            try {
                final Config config = Config.getInstance();
                scheduler = new LocalScheduler(config.getProjects());
            } catch (Exception e) {
                e.printStackTrace();
            }

            shell.getDisplay().asyncExec(new SchedulerStartedRunnable(scheduler));
        }
    }

    /**
     * Runs on the UI thread to handle the scheduler starting.
     */
    private final class SchedulerStartedRunnable implements Runnable {
        private final Scheduler mScheduler;

        public SchedulerStartedRunnable(Scheduler scheduler) {
            mScheduler = scheduler;
        }

        @Override
        public void run() {
            if (mIsShuttingDown) {
                // Don't set up the new scheduler -- go directly to stopped
                statusLbl.setText("Shutting down...");
                mSchedulerState = SchedulerState.NoScheduler;
                shell.dispose();
            } else if (mRestartWhenStarted) {
                // Don't set up the new scheduler -- go directly to starting
                statusLbl.setText("Waiting for the task scheduler to start...");
                mRestartWhenStarted = false;
                mSchedulerState = SchedulerState.Starting;
                new Thread(new StartSchedulerRunnable()).start();
            } else {
                if (mScheduler != null) {
                    // Set up the new scheduler
                    statusLbl.setText("Task scheduler is running");

                    SchedulerForm.this.mScheduler = mScheduler;
                    rebuildTaskGroups();
                    mScheduler.addSchedulerEventListener(mSchedulerEventListener);
                    mThread = new Thread() {
                        @Override
                        public void run() {
                            mScheduler.start();
                        }
                    };
                    mThread.start();
                    //mScheduler.start();

                    mSchedulerState = SchedulerState.Running;
                } else {
                    // Starting failed
                    statusLbl.setText("Tash scheduler failed to start -- find a programmer!");

                    final MessageBox mb = new MessageBox(shell);
                    mb.setMessage("Failed to load projects");
                    mb.open();

                    mSchedulerState = SchedulerState.NoScheduler;
                }
            }
        }
    }

    /**
     * Runs on a background thread to stop the scheduler.
     */
    private final class StopSchedulerRunnable implements Runnable {
        @Override
        public void run() {
            // FIXME: make it work
            mScheduler.stop();
            mScheduler.join();
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            shell.getDisplay().asyncExec(new SchedulerStoppedRunnable());
        }
    }

    /**
     * Runs on the UI thread to handle the scheduler stopping.
     */
    private final class SchedulerStoppedRunnable implements Runnable {
        @Override
        public void run() {
            mScheduler.removeSchedulerEventListener(mSchedulerEventListener);
            mScheduler = null;
            clearTasks();

            if (mIsShuttingDown) {
                // Stay stopped when shutting down
                statusLbl.setText("Shutting down...");
                mSchedulerState = SchedulerState.NoScheduler;
                shell.dispose();
            } else {
                // If not shutting down, restart the scheduler
                statusLbl.setText("Waiting for the task scheduler to start...");
                mSchedulerState = SchedulerState.Starting;
                new Thread(new StartSchedulerRunnable()).start();
            }
        }
    }

    private void rebuildTaskGroups() {
        // Clean up all existing task group items, progress bars, and editors
        for (TableItem item : taskGroups.values()) {
            final ProgressBar progressBar = (ProgressBar)item.getData();
            final TableEditor editor = (TableEditor)progressBar.getData();
            editor.dispose();
            progressBar.dispose();
            item.dispose();
        }

        // Create new items
        for (String name : mScheduler.getTaskGroupNames()) {
            TableItem item = new TableItem(groupTbl, SWT.NONE);
            item.setText(new String[] {
                    name,
                    "0/0",
                    ""
            });

            ProgressBar progressBar = new ProgressBar(groupTbl, SWT.NONE);

            TableEditor editor = new TableEditor(groupTbl);
            editor.grabHorizontal = editor.grabVertical = true;
            editor.setEditor(progressBar, item, TASK_GROUP_PROGRESS_BAR_COLUMN_INDEX);

            item.setData(progressBar);
            progressBar.setData(editor);

            taskGroups.put(name, item);
        }
    }

    private void clearTasks() {
        for (TaskEntry entry : mTasks.values()) {
            entry.dispose();
        }
        mTasks.clear();
        updateLayouts();
    }

    /**
     * Handles selection events sent to the "View > Query window" menu item.
     */
    private final class QueryItemSelectionListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            Query queryForm = new Query(shell);
            queryForm.open();
        }
    }

}