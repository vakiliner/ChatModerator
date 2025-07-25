package vakiliner.chatmoderator.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.google.common.collect.Maps;

public class AutoModerationRule {
	private String name;
	private final TriggerType triggerType;
	private boolean enabled = false;
	private final TriggerMetadata triggerMetadata = new TriggerMetadata();
	private final Actions actions = new Actions();

	public AutoModerationRule(String name, TriggerType triggerType) {
		this.name = Objects.requireNonNull(name);
		this.triggerType = Objects.requireNonNull(triggerType);
	}

	public String getName() {
		return this.name;
	}

	public TriggerType getTriggerType() {
		return this.triggerType;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public TriggerMetadata getTriggerMetadata() {
		return this.triggerMetadata;
	}

	public Actions getActions() {
		return this.actions;
	}

	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public static enum TriggerType {
		MESSAGE(1);

		private static final Map<Integer, TriggerType> BY_INT = Maps.newHashMap();
		private final byte type;

		private TriggerType(int type) {
			this.type = (byte) type;
		}

		public byte get() {
			return this.type;
		}

		public static TriggerType getByInt(int type) {
			return BY_INT.get(type);
		}

		static {
			for (TriggerType triggerType : values()) {
				BY_INT.put((int) triggerType.type, triggerType);
			}
		}
	}

	public static class TriggerMetadata {
		private List<String> keywordFilter = Collections.emptyList();
		private List<Pattern> regexPatterns = Collections.emptyList();
		private List<String> allowList = Collections.emptyList();

		public List<String> getKeywordFilter() {
			return this.keywordFilter;
		}

		public List<Pattern> getRegexPatterns() {
			return this.regexPatterns;
		}

		public List<String> getAllowList() {
			return this.allowList;
		}

		public void setKeywordFilter(Collection<String> keywordFilter) {
			this.keywordFilter = Collections.unmodifiableList(new ArrayList<>(keywordFilter));
		}

		public void setRegexPatterns(Collection<String> regexPatterns) {
			this.regexPatterns = Collections.unmodifiableList(regexPatterns.stream().map((regex) -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)).collect(Collectors.toList()));
		}

		public void setAllowList(Collection<String> allowList) {
			this.allowList = Collections.unmodifiableList(new ArrayList<>(allowList));
		}
	}

	public static class Actions {
		private boolean blockAction;
		private int muteTime;
		private boolean logAdmins;
		private String customBlockMessage;

		public boolean blockAction() {
			return this.blockAction;
		}

		public boolean mutePlayer() {
			return this.muteTime > 0;
		}

		public boolean logAdmins() {
			return this.logAdmins;
		}

		public String customBlockMessage() {
			return this.customBlockMessage;
		}

		public int muteTime() {
			return this.muteTime;
		}

		public void blockAction(boolean blockAction) {
			this.blockAction = blockAction;
		}

		public void muteTime(int muteTime) {
			if (muteTime < 0) throw new IllegalArgumentException();
			this.muteTime = muteTime;
		}

		public void logAdmins(boolean logAdmins) {
			this.logAdmins = logAdmins;
		}

		public void customBlockMessage(String customBlockMessage) {
			this.customBlockMessage = customBlockMessage;
		}
	}

	public Object checkText(String rawMessage, Map<Character, String> cleaner) {
		final List<String> words;
		final List<String> cleanedWords;
		{
			List<StringBuilder> wordsBuilder = new ArrayList<>();
			List<StringBuilder> cleanedWordsBuilder = new ArrayList<>();
			wordsBuilder.add(new StringBuilder());
			cleanedWordsBuilder.add(new StringBuilder());
			for (char ch : rawMessage.toCharArray()) {
				int index = wordsBuilder.size() - 1;
				if (ch == ' ' || ch == '\n') {
					if (ch != ' ' || wordsBuilder.get(index).length() > 0) {
						wordsBuilder.add(new StringBuilder());
						cleanedWordsBuilder.add(new StringBuilder());
					}
					continue;
				}
				wordsBuilder.get(index).append(ch);
				cleanedWordsBuilder.get(index).append(cleaner.getOrDefault(ch, Character.toString(ch)));
			}
			words = new ArrayList<>(wordsBuilder.stream().map(StringBuilder::toString).collect(Collectors.toList()));
			cleanedWords = new ArrayList<>(cleanedWordsBuilder.stream().map(StringBuilder::toString).collect(Collectors.toList()));
		}
		int size = words.size();
		TriggerMetadata metadata = this.triggerMetadata;
		for (String rawAllow : metadata.allowList) {
			byte flags = parseFlags(rawAllow);
			String allow = (flags > 0 ? rawAllow.replaceAll("^\\*\\s*|\\s*\\*$", "") : rawAllow).toLowerCase();
			int count = allow.split(" ").length;
			LinkedList<String> list = new LinkedList<>();
			for (int i = 0; size > i; i++) {
				String word = words.get(i);
				if (word == null) {
					list.clear();
					continue;
				}
				list.add(word);
				if (list.size() < count) continue;
				if (list.size() > count) list.removeFirst();
				String check = String.join(" ", list);
				if (checkWord(check.toLowerCase(), allow, flags)) {
					list.clear();
					for (int r = 0; count > r; r++) {
						words.set(i - r, null);
						cleanedWords.set(i - r, null);
					}
				}
			}
		}
		for (String rawKeyword : metadata.keywordFilter) {
			byte flags = parseFlags(rawKeyword);
			String keyword = (flags > 0 ? rawKeyword.replaceAll("^\\*\\s*|\\s*\\*$", "") : rawKeyword).toLowerCase();
			int count = keyword.split(" ").length;
			LinkedList<String> list = new LinkedList<>();
			LinkedList<String> cleaned = new LinkedList<>();
			for (int i = 0; size > i; i++) {
				String word = words.get(i);
				String cleanedWord = cleanedWords.get(i);
				if (word == null) {
					list.clear();
					cleaned.clear();
					continue;
				}
				list.add(word);
				cleaned.add(cleanedWord);
				if (list.size() < count) continue;
				if (list.size() > count) {
					list.removeFirst();
					cleaned.removeFirst();
				}
				String check = String.join(" ", list);
				String cleanedCheck = String.join(" ", cleaned);
				if (checkWord(check.toLowerCase(), keyword, flags) || checkWord(cleanedCheck.toLowerCase(), keyword, flags)) {
					return new Object();
				}
			}
		}
		final List<String> message;
		final List<String> cleanedMessage;
		{
			List<StringBuilder> messageBuilder = new ArrayList<>();
			List<StringBuilder> cleanedMessageBuilder = new ArrayList<>();
			messageBuilder.add(new StringBuilder());
			cleanedMessageBuilder.add(new StringBuilder());
			for (int i = 0; i < size; i++) {
				String word = words.get(i);
				if (word == null) {
					if (messageBuilder.get(messageBuilder.size() - 1).length() > 0) {
						messageBuilder.add(new StringBuilder());
					}
					if (cleanedMessageBuilder.get(cleanedMessageBuilder.size() - 1).length() > 0) {
						cleanedMessageBuilder.add(new StringBuilder());
					}
					continue;
				}
				messageBuilder.get(messageBuilder.size() - 1).append(word);
				cleanedMessageBuilder.get(cleanedMessageBuilder.size() - 1).append(cleanedWords.get(i));
			}
			message = messageBuilder.stream().map(StringBuilder::toString).collect(Collectors.toList());
			cleanedMessage = cleanedMessageBuilder.stream().map(StringBuilder::toString).collect(Collectors.toList());
		}
		for (Pattern pattern : metadata.regexPatterns) {
			for (String msg : message) {
				if (pattern.matcher(msg).find()) {
					return new Object();
				}
			}
			for (String msg : cleanedMessage) {
				if (pattern.matcher(msg).find()) {
					return new Object();
				}
			}
		}
		return null;
	}

	private static byte parseFlags(String word) {
		return (byte) ((word.endsWith("*") ? 1 : 0) + (word.startsWith("*") ? 2 : 0));
	}

	private static boolean checkWord(String word, String keyword, byte flags) {
		switch (flags) {
			case 0: return word.equals(keyword);
			case 1: return word.startsWith(keyword);
			case 2: return word.endsWith(keyword);
			case 3: return word.contains(keyword);
			default: throw new IllegalArgumentException("Invalid flag");
		}
	}
}