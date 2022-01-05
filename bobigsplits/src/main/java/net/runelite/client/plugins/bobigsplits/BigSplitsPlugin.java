package net.runelite.client.plugins.bobigsplits;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//coded by Boak
//help from Big Mfkn Tyler, Caps and BL dev community

@Slf4j
@Extension
@PluginDescriptor(
	name = "[Bo] Big Splits",
	description = "Highlights tile where a big nylo died and smalls are likely to spawn",
	tags = {"big", "splits" , "nylo", "boak" , "Boak"},
	enabledByDefault = false
)
public class BigSplitsPlugin extends Plugin {
	@Inject private Client client;
	@Inject private OverlayManager overlayManager;

	@Inject
	private BigSplitsConfig config;


	@Inject private BigSplitsOverlay overlay;

	@Getter
	private final Map<NPC, Integer> splitsMap = new HashMap<>();
	private final Set<NPC> bigNylos = new HashSet<>();

	public boolean mirrorMode;

	@Provides
	BigSplitsConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(BigSplitsConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		bigNylos.clear();
		splitsMap.clear();
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (!splitsMap.isEmpty()) {
			splitsMap.values().removeIf((value) -> value <= 1);
			splitsMap.replaceAll((key, value) -> value - 1);
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (!bigNylos.isEmpty() && event.getActor() instanceof NPC) {
			NPC npc = (NPC)event.getActor();
			if (bigNylos.contains(npc)) {
				int anim = npc.getAnimation();
				if (anim == 8005 || anim == 7991 || anim == 7998) {
					splitsMap.putIfAbsent(npc, 6);
					bigNylos.remove(npc);
				}
				if (anim == 8006 || anim == 7992 || anim == 8000)
				{
					splitsMap.putIfAbsent(npc, 4);
					bigNylos.remove(npc);
				}
			}
		}
	}

	@Subscribe
	private void onNpcSpawned(NpcSpawned e) {
		final NPC npc = e.getNpc();
		final int id = npc.getId();
		switch (id) {
			case NpcID.NYLOCAS_ISCHYROS_8345: //Normal mode
			case NpcID.NYLOCAS_TOXOBOLOS_8346:
			case NpcID.NYLOCAS_HAGIOS_8347:
			case NpcID.NYLOCAS_ISCHYROS_10777: //Story mode
			case NpcID.NYLOCAS_TOXOBOLOS_10778:
			case NpcID.NYLOCAS_HAGIOS_10779:
			case NpcID.NYLOCAS_ISCHYROS_10794: //Hard mode
			case NpcID.NYLOCAS_TOXOBOLOS_10795:
			case NpcID.NYLOCAS_HAGIOS_10796:
			case 10800:
			case 10801:
			case 10802:
				bigNylos.add(npc);
				break;
		}
	}

	/*@Subscribe
	private void onClientTick(ClientTick event) {
		if (client.isMirrored() && !mirrorMode) {
			overlay.setLayer(OverlayLayer.AFTER_MIRROR);
			overlayManager.remove(overlay);
			overlayManager.add(overlay);
			mirrorMode = true;
		}
	}*/
}
