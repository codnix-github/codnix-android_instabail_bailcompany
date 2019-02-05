package com.bailcompany.utils;

import android.content.Context;
import android.content.res.Resources;


import com.bailcompany.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;

public class Dates {

	// < 1 min - x seconds ago
	// < 1 hour - x minutes ago
	// < 1 day - x hours ago
	// after yesterday 0 am - Yesterday
	// < 30 days - x days ago
	// < 12 months - x months ago
	// else - x years y months ago
	// *else - MMM dd yyyy, HH:mm
	public static String formatDate(Context context, DateTime sourceDate) {
		Resources res = context.getResources();
		DateTime dateLocalZone = sourceDate.withZone(DateTimeZone.getDefault());
		DateTime dateUTC = sourceDate.withZone(DateTimeZone.UTC);
		DateTime nowUTC = DateTime.now(DateTimeZone.UTC);
		if (dateUTC.compareTo(nowUTC) < 0) {

			int secondsAgo = Seconds.secondsBetween(dateUTC, nowUTC).getSeconds();
			if (secondsAgo < 60) {

				// X seconds ago
				return res.getString(R.string._ago,
						String.valueOf(secondsAgo) + " " + res.getQuantityString(R.plurals.seconds, secondsAgo));

			} else if (secondsAgo < 60 * 60) {

				// X minutes ago
				int minutesAgo = secondsAgo / 60;
				return res.getString(R.string._ago,
						String.valueOf(minutesAgo) + " " + res.getQuantityString(R.plurals.minutes, minutesAgo));

			} else if (secondsAgo < 24 * 60 * 60) {

				// X hours ago
				int hoursAgo = secondsAgo / (60 * 60);
				return res.getString(R.string._ago,
						String.valueOf(hoursAgo) + " " + res.getQuantityString(R.plurals.hours, hoursAgo));

			} else {

				DateTime yesterdayStartUTC = nowUTC.minusDays(1).withTimeAtStartOfDay();
				if (dateUTC.compareTo(yesterdayStartUTC) >= 0) {

					// Yesterday
					return res.getString(R.string.yesterday);

				} else {

					int daysAgo = Days.daysBetween(dateUTC, nowUTC).getDays();
					if (daysAgo <= 30) {

						// X days ago
						return res.getString(R.string._ago,
								String.valueOf(daysAgo) + " " + res.getQuantityString(R.plurals.days, daysAgo));

					} else {

						int monthsAgo = Months.monthsBetween(dateUTC, nowUTC).getMonths();
						if (monthsAgo < 12) {

							// X months ago
							return res.getString(R.string._ago,
									String.valueOf(monthsAgo) + " " + res.getQuantityString(R.plurals.months, monthsAgo));

						} else {

							int years = monthsAgo / 12;
							int months = monthsAgo % 12;

							// X years Y months ago
							return res.getString(R.string._ago,
									String.valueOf(years) + " " + res.getQuantityString(R.plurals.years, years) + " "
											+ String.valueOf(months) + " " + res.getQuantityString(R.plurals.months, months));
						}

					}

				}

			}

		}
		// dd MMMM YYYY, hh:mm
		return DateTimeFormat.forPattern("MMM dd yyyy, HH:mm").print(dateLocalZone);
	}

	public static String formatDateTimeForMessage(DateTime sourceDateTime) {
		if (sourceDateTime == null) return "";
		DateTime dateTimeLocalZone = sourceDateTime.withZone(DateTimeZone.getDefault());
		return DateTimeFormat.forPattern("EEE, MMM d, h:mm a").print(dateTimeLocalZone);
	}

	private static final long SECOND_MILLIS = 1000;
	private static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
	private static final long WEEK_MILLIS = 7 * DAY_MILLIS;
	private static final long MONTH_MILLS = 30 * DAY_MILLIS;
	private static final long YEAR_MILLS = 12 * MONTH_MILLS;

	public static String getTimeAgo(long time) {

		if (time < 1000000000000L) {
			// if timestamp given in seconds, convert to millis
			time *= 1000;
		}

		long now = System.currentTimeMillis();
		if (time > now || time <= 0) {
			return "just now";
		}

		// TODO: localize
		final long diff = now - time;
		if (diff < MINUTE_MILLIS) {
			return "just now";
		} else if (diff < 2 * MINUTE_MILLIS) {
			return "a minute ago";
		} else if (diff < 50 * MINUTE_MILLIS) {
			return diff / MINUTE_MILLIS + " minutes ago";
		} else if (diff < 90 * MINUTE_MILLIS) {
			return "an hour ago";
		} else if (diff < 24 * HOUR_MILLIS) {
			return diff / HOUR_MILLIS + " hours ago";
		} else if (diff < 48 * HOUR_MILLIS) {
			return "yesterday";
		} else if (diff < WEEK_MILLIS) {
			return diff / DAY_MILLIS + " days ago";
		} else if (diff < 2 * WEEK_MILLIS) {
			return "last week";
		} else if (diff < MONTH_MILLS) {
			return diff / WEEK_MILLIS + " weeks ago";
		} else if (diff < 2 * MONTH_MILLS) {
			return "a month ago";
		} else if (diff < YEAR_MILLS) {
			return diff / MONTH_MILLS + " months ago";
		} else if (diff < 2 * YEAR_MILLS){
			return "last year";
		} else {
			return diff / YEAR_MILLS + " years ago";
		}

	}
}