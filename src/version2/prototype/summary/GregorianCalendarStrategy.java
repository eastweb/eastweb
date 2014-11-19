package version2.prototype.summary;

import java.util.Calendar;

public class GregorianCalendarStrategy implements CalendarStrategy {

    @Override
    public Calendar getStartDate(Calendar projectSDate) throws Exception {
        return projectSDate;
    }

}
