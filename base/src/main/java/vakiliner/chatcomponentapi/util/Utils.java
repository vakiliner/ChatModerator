package vakiliner.chatcomponentapi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static String stringFormat(String string, Object... rawArgs) {
		StringBuilder builder = new StringBuilder();
		Matcher matcher = Pattern.compile("%((?<s>\\d+)\\$)?s").matcher(string);
		int index = 0;
		int i = 0;
		while (matcher.find(index)) {
			builder.append(string.substring(index, matcher.start()));
			String rawS = matcher.group("s");
			final int s;
			if (rawS != null) {
				s = Integer.parseInt(rawS) - 1;
			} else {
				s = i++;
			}
			builder.append(s >= 0 && s < rawArgs.length ? rawArgs[s] : matcher.group()); 
			index = matcher.end();
		}
		builder.append(string.substring(index));
		return builder.toString();
	}
}