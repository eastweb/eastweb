package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class FloatVerifyListener implements Listener {

    @Override
    public void handleEvent(Event event) {
        Text text = (Text)event.widget;
        String string = text.getText().substring(0, event.start) + event.text + text.getText().substring(event.end);
        try {
            Float.parseFloat(string);
        } catch(NumberFormatException ex) {
            event.doit = false;
        }
    }

}