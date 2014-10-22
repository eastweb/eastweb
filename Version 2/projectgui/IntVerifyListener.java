package edu.sdstate.eastweb.prototype.projectgui;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class IntVerifyListener implements Listener {

    @Override
    public void handleEvent(Event event) {
        char[] chars = new char[event.text.length()];
        event.text.getChars(0, chars.length, chars, 0);
        for (int i=0; i<chars.length; i++) {
            if (!Character.isDigit(chars[i])) {
                event.doit = false;
                return;
            }
        }
    }

}
