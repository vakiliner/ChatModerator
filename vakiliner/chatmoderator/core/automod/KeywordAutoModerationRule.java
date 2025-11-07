package vakiliner.chatmoderator.core.automod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import vakiliner.chatmoderator.api.GsonAutoModerationRule;
import vakiliner.chatmoderator.api.GsonKeywordTriggerMetadata;
import vakiliner.chatmoderator.base.ChatPlayer;
import vakiliner.chatmoderator.core.AutoModeration;

public class KeywordAutoModerationRule extends BaseAutoModerationRule {
	private final TriggerMetadata triggerMetadata = new TriggerMetadata();

	public KeywordAutoModerationRule(AutoModeration automod, String name, EventType eventType) {
		super(automod, name, eventType);
	}

	public KeywordAutoModerationRule(AutoModeration automod, GsonAutoModerationRule rule) {
		super(automod, rule);
		GsonKeywordTriggerMetadata triggerMetadata = new Gson().fromJson(rule.trigger_metadata, GsonKeywordTriggerMetadata.class);
		if (triggerMetadata.keyword_filter != null) this.triggerMetadata.setKeywordFilter(triggerMetadata.keyword_filter);
		if (triggerMetadata.regex_patterns != null) this.triggerMetadata.setRegexPatterns(triggerMetadata.regex_patterns);
		if (triggerMetadata.allow_list != null) this.triggerMetadata.setAllowList(triggerMetadata.allow_list);
	}

	public TriggerType<KeywordAutoModerationRule> getTriggerType() {
		return TriggerType.KEYWORD;
	}

	protected boolean isSupportEventType(EventType eventType) {
		return eventType == EventType.MESSAGE;
	}

	public TriggerMetadata getTriggerMetadata() {
		return this.triggerMetadata;
	}

	public MatchResult checkText(ChatPlayer player, String rawMessage) {
		Map<Character, String> cleaner = this.automod.cleaner;
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
				if (checkWord(check.toLowerCase(), keyword, flags)) {
					return new MatchResult(keyword, check);
				}
				if (checkWord(cleanedCheck.toLowerCase(), keyword, flags)) {
					return new MatchResult(keyword, cleanedCheck);
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
			String keyword = pattern.pattern();
			for (String msg : message) {
				Matcher matcher = pattern.matcher(msg);
				if (matcher.find()) {
					return new MatchResult(keyword, matcher.group());
				}
			}
			for (String msg : cleanedMessage) {
				Matcher matcher = pattern.matcher(msg);
				if (matcher.find()) {
					return new MatchResult(keyword, matcher.group());
				}
			}
		}
		return null;
	}

	private static byte parseFlags(String word) {
		return (byte) ((word.endsWith("*") ? 1 : 0) | (word.startsWith("*") ? 2 : 0));
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
}