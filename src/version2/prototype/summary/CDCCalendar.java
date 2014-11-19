package version2.prototype.summary;

import java.util.Calendar;

public class CDCCalendar implements CalendarStrategy {

    @Override
    public Calendar getStartDate(Calendar projectSDate) throws Exception {
        projectSDate.getFirstDayOfWeek();

        return null;
    }

}
