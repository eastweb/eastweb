package version2.prototype.summary;

import java.util.Calendar;

public class CDCCalendarStrategy implements CalendarStrategy {

    @Override
    public Calendar getStartDate(Calendar projectSDate) throws Exception {
        int firstDay = Calendar.SUNDAY;
        int currentDay = projectSDate.get(Calendar.DAY_OF_WEEK);
        if(currentDay !=  firstDay){
            Calendar newDate = (Calendar) projectSDate.clone();

            do{
                newDate.add(Calendar.DAY_OF_MONTH, 1);
                currentDay = newDate.get(Calendar.DAY_OF_WEEK);
            }while(currentDay != firstDay);

            return newDate;
        }
        return projectSDate;
    }

}
