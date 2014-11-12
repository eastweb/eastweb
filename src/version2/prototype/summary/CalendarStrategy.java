package version2.prototype.summary;

import java.util.Calendar;

public interface CalendarStrategy {
    Calendar getStartDate(Calendar projectSDate) throws Exception;
}
