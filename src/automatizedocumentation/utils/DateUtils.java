package automatizedocumentation.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Util class to work with date objects.
 * 
 * @author Daniane P. Gomes
 *
 */
public class DateUtils {

	private DateUtils() {

	}

	/**
	 * Converts an object of type Date to LocalDate
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate toLocalDate(final Date date) {

		final Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Converts an object of type LocalDate to Date
	 *
	 * @param localDate
	 * @return
	 */
	public static Date toDate(final LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static DateTimeFormatter getDateFormat() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

}
