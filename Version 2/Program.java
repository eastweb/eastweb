package edu.sdstate.eastweb.prototype;

import org.eclipse.swt.widgets.Display;
import edu.sdstate.eastweb.prototype.scheduler.SchedulerForm;

public class Program {

    public static void main(String[] args) {
        final Display display = new Display();

        final SchedulerForm schedulerForm = new SchedulerForm(display);
        schedulerForm.open();

        // Standard SWT message loop
        while (!schedulerForm.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

}