package vakiliner.chatmoderator.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import vakiliner.chatmoderator.core.AutoModeration.CheckResult;
import vakiliner.chatmoderator.core.automod.MessageActions;

/**
 * This event is called when automod is triggered.
 * The event fires before the automod actions are applied and can be canceled.
 * Instead of canceling the event, you can change the actions that are applied.
 * To cancel the event, return false.
 * If you want your code to run when the event is not cancelled, add the following to the top of your code:
 * <pre><code>
 * if (!result) return result;
 * </code></pre>
 */
public interface AutoModerationTriggerCallback {
	Event<AutoModerationTriggerCallback> EVENT = EventFactory.createArrayBacked(AutoModerationTriggerCallback.class, (callbacks) -> (player, checkResult, actions, result) -> {
		for (AutoModerationTriggerCallback callback : callbacks) {
			result = callback.trigger(player, checkResult, actions, result);
		}
		return result;
	});

	boolean trigger(ServerPlayer player, CheckResult checkResult, MessageActions actions, boolean result);
}