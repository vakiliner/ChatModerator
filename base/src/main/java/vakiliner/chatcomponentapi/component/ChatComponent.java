package vakiliner.chatcomponentapi.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;

public abstract class ChatComponent implements ChatHoverEvent.IContent {
	protected ChatStyle style;
	protected List<ChatComponent> extra;

	public ChatComponent() {
		this(ChatStyle.EMPTY);
	}

	public ChatComponent(ChatStyle style) {
		this.style = Objects.requireNonNull(style);
	}

	public ChatComponent(ChatTextColor color) {
		this(ChatStyle.EMPTY.withColor(color));
	}

	public ChatComponent(ChatComponentFormat format) {
		this(ChatStyle.EMPTY.withFormat(format));
	}

	protected ChatComponent(ChatComponent component, boolean cloneExtra) {
		this.style = component.style;
		if (cloneExtra) {
			List<ChatComponent> clone = component.extra;
			if (clone != null) {
				List<ChatComponent> extra = this.extra = new ArrayList<>();
				clone.forEach((c) -> extra.add(c.clone(true)));
			}
		} else {
			this.extra = component.extra;
		}
	}

	public ChatComponent clone() {
		return this.clone(true);
	}

	public abstract ChatComponent clone(boolean cloneExtra);

	public String toLegacyText() {
		return this.toLegacyText(null, Collections.emptySet());
	}

	protected String toLegacyText(final ChatTextColor parentColor, final Set<ChatComponentFormat> parentFormats) {
		StringBuilder text = new StringBuilder();
		Set<ChatComponentFormat> formats = new HashSet<>(parentFormats);
		ChatStyle style = this.style;
		ChatTextColor color = style.getColor();
		if (color == null) color = parentColor;
		boolean reset = !Objects.equals(color, parentColor);
		for (Map.Entry<ChatComponentFormat, Boolean> entry : style.getFormats().entrySet()) {
			Boolean isSet = entry.getValue();
			if (isSet != null) {
				if (isSet) {
					formats.add(entry.getKey());
				} else if (formats.remove(entry.getKey())) {
					reset = true;
				}
			}
		}
		formats = Collections.unmodifiableSet(formats);
		if (reset) {
			ChatTextFormat textColor = color != null ? color.asFormat() : ChatTextFormat.RESET;
			text.append(textColor != null ? textColor : ChatTextFormat.RESET);
			for (ChatComponentFormat format : formats) {
				text.append(format.asTextFormat());
			}
		} else for (ChatComponentFormat format : formats) {
			if (!parentFormats.contains(format)) {
				text.append(format.asTextFormat());
			}
		}
		text.append(this.getLegacyText(color, formats));
		List<ChatComponent> extra = this.getExtra();
		if (extra != null) for (ChatComponent component : extra) {
			text.append(component.toLegacyText(color, formats));
		}
		if (!(reset = !Objects.equals(color, parentColor))) for (ChatComponentFormat format : formats) {
			if (!parentFormats.contains(format)) {
				reset = true;
				break;
			}
		}
		if (reset) {
			text.append(parentColor != null ? parentColor.asFormat() : ChatTextFormat.RESET);
			for (ChatComponentFormat format : parentFormats) {
				text.append(format);
			}
		} else for (ChatComponentFormat format : parentFormats) {
			if (!formats.contains(format)) {
				text.append(format);
			}
		}
		return text.toString();
	}

	protected abstract String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats);

	public ChatStyle getStyle() {
		return this.style;
	}

	@Deprecated
	public ChatTextColor getColorRaw() {
		return this.style.getColor();
	}

	@Deprecated
	public Boolean isBoldRaw() {
		return this.style.getBold();
	}

	@Deprecated
	public Boolean isItalicRaw() {
		return this.style.getItalic();
	}

	@Deprecated
	public Boolean isUnderlinedRaw() {
		return this.style.getUnderlined();
	}

	@Deprecated
	public Boolean isStrikethroughRaw() {
		return this.style.getStrikethrough();
	}

	@Deprecated
	public Boolean isObfuscatedRaw() {
		return this.style.getObfuscated();
	}

	@Deprecated
	public String getInsertion() {
		return this.style.getInsertion();
	}

	@Deprecated
	public ChatClickEvent getClickEvent() {
		return this.style.getClickEvent();
	}

	@Deprecated
	public ChatHoverEvent<?> getHoverEvent() {
		return this.style.getHoverEvent();
	}

	public List<ChatComponent> getExtra() {
		List<ChatComponent> extra = this.extra;
		return extra != null ? Collections.unmodifiableList(extra) : null;
	}

	@Deprecated
	public Boolean getFormatRaw(ChatComponentFormat format) {
		return this.style.getFormat(format);
	}

	@Deprecated
	public Map<ChatComponentFormat, Boolean> getFormatsRaw() {
		return this.style.getFormats();
	}

	public void setStyle(ChatStyle style) {
		this.style = Objects.requireNonNull(style);
	}

	@Deprecated
	public void setColor(ChatTextColor color) {
		this.setStyle(this.style.withColor(color));
	}

	@Deprecated
	public void setBold(Boolean bold) {
		this.setStyle(this.style.withBold(bold));
	}

	@Deprecated
	public void setItalic(Boolean italic) {
		this.setStyle(this.style.withItalic(italic));
	}

	@Deprecated
	public void setUnderlined(Boolean underlined) {
		this.setStyle(this.style.withUnderlined(underlined));
	}

	@Deprecated
	public void setStrikethrough(Boolean strikethrough) {
		this.setStyle(this.style.withStrikethrough(strikethrough));
	}

	@Deprecated
	public void setObfuscated(Boolean obfuscated) {
		this.setStyle(this.style.withObfuscated(obfuscated));
	}

	@Deprecated
	public void setInsertion(String insertion) {
		this.setStyle(this.style.withInsertion(insertion));
	}

	@Deprecated
	public void setClickEvent(ChatClickEvent clickEvent) {
		this.setStyle(this.style.withClickEvent(clickEvent));
	}

	@Deprecated
	public void setHoverEvent(ChatHoverEvent<?> hoverEvent) {
		this.setStyle(this.style.withHoverEvent(hoverEvent));
	}

	@Deprecated
	public void setExtra(Collection<ChatComponent> children) {
		if (children == null) {
			this.extra = null;
		} else {
			this.extra = new ArrayList<>(children);
		}
	}

	@Deprecated
	public void setFormat(ChatComponentFormat format, Boolean isSet) {
		this.setStyle(this.style.withFormat(format, isSet));
	}

	@Deprecated
	public void setFormats(Map<ChatComponentFormat, Boolean> map) {
		this.setStyle(this.style.withFormats(map));
	}

	public void append(ChatComponent component) {
		if (component == this) throw new IllegalArgumentException("This component cannot be added");
		List<ChatComponent> extra = component.extra;
		if (extra != null) {
			Set<ChatComponent> checked = new HashSet<>();
			Queue<ChatComponent> queue = new LinkedList<>(extra);
			ChatComponent current = null;
			while ((current = queue.poll()) != null) {
				if (checked.contains(current)) continue;
				if (current == this) throw new IllegalArgumentException("Components are looping, try cloning the component before appending");
				List<ChatComponent> e = current.extra;
				if (e != null) queue.addAll(e);
				checked.add(current);
			}
		}
		this.unsafeAppend(component);
	}

	protected void unsafeAppend(ChatComponent component) {
		List<ChatComponent> extra = this.extra;
		if (extra == null)  {
			extra = this.extra = new ArrayList<>();
		}
		extra.add(component);
	}

	@SuppressWarnings("deprecation")
	public ChatComponentWithLegacyText withLegacyComponent(Supplier<ChatComponent> getLegacyComponent) {
		return new ChatComponentWithLegacyText(this, getLegacyComponent);
	}

	@SuppressWarnings("deprecation")
	public ChatComponentWithLegacyText withLegacyComponent(ChatComponent legacyComponent) {
		return new ChatComponentWithLegacyText(this, legacyComponent);
	}

	public ChatComponentWithLegacyText withLegacyText(Supplier<String> getLegacyText) {
		return this.withLegacyComponent(() -> new ChatTextComponent(getLegacyText.get()));
	}

	@SuppressWarnings("deprecation")
	public ChatComponentWithLegacyText withLegacyText(String legacyText) {
		return new ChatComponentWithLegacyText(this, new ChatTextComponent(legacyText));
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatComponent)) {
			return false;
		} else {
			ChatComponent other = (ChatComponent) obj;
			return this.style.equals(other.style) && (this.extra == other.extra || (this.extra == null || this.extra.isEmpty() ? other.extra == null || other.extra.isEmpty() : this.extra.equals(other.extra)));
		}
	}

	public JsonElement serialize() {
		return serialize(this);
	}

	protected abstract void serialize(JsonObject object);

	public static JsonElement serialize(ChatComponent component) {
		JsonObject object = ChatStyle.serialize(component.getStyle(), new JsonObject());
		List<ChatComponent> extra = component.getExtra();
		if (extra != null && !extra.isEmpty()) {
			JsonArray array = new JsonArray();
			extra.forEach((c) -> array.add(ChatComponent.serialize(c)));
			object.add("extra", array);
		}
		component.serialize(object);
		return object;
	}

	protected static <Component extends ChatComponent> Component deserialize(Function<ChatStyle, Component> function, JsonObject object) {
		ChatStyle style = ChatStyle.deserialize(object);
		Component component = function.apply(style);
		JsonElement extra = object.get("extra");
		if (extra != null) {
			JsonArray array = extra.getAsJsonArray();
			array.forEach((c) -> component.unsafeAppend(ChatComponent.deserialize(c)));
		}
		return component;
	}

	public static ChatComponent deserialize(JsonElement element) {
		return getDeserializer(element).apply(element);
	}

	private static Function<JsonElement, ChatComponent> getDeserializer(JsonElement element) {
		if (element.isJsonPrimitive()) {
			return ChatTextComponent::deserialize;
		}
		JsonObject object = element.getAsJsonObject();
		if (object.has("text")) {
			return ChatTextComponent::deserialize;
		} else if (object.has("translate")) {
			return ChatTranslateComponent::deserialize;
		} else if (object.has("selector")) {
			return ChatSelectorComponent::deserialize;
		} else {
			return null;
		}
	}
}