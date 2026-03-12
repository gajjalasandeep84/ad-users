package com.example.adusersdemo.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class AdTimeUtil {
	// AD FileTime starts from 1601-01-01
	private static final long WINDOWS_EPOCH = -11644473600000L; // diff between 1601 and 1970 in ms

	public static LocalDateTime fromFileTime(String fileTimeStr) {
		long fileTime = Long.parseLong(fileTimeStr); // e.g. 130777514772153820
		long millis = (fileTime / 10000L) + WINDOWS_EPOCH;
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
	}

	public static String parseAccountExpires(String fileTimeStr) {
		try {
			long fileTime = Long.parseLong(fileTimeStr);
			if (fileTime == 0 || fileTime == Long.MAX_VALUE) {
				return "Never Expires";
			}
			long millis = (fileTime / 10000L) - 11644473600000L;
			LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
			return dt.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
