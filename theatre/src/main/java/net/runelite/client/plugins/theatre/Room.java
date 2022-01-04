/*
 * THIS PLUGIN WAS WRITTEN BY A KEYBOARD-WIELDING MONKEY BOI BUT SHUFFLED BY A KANGAROO WITH THUMBS.
 * The plugin and it's refactoring was intended for xKylee's Externals but I'm sure if you're reading this, you're probably planning to yoink..
 * or you're just genuinely curious. If you're trying to yoink, it doesn't surprise me.. just don't claim it as your own. Cheers.
 */

package net.runelite.client.plugins.theatre;

import net.runelite.api.Client;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.function.Supplier;

@Singleton
public abstract class Room
{
	protected final TheatrePlugin plugin;
	protected final TheatreConfig config;

	@Inject
	protected Client client;
	@Inject
	protected EventBus eventBus;
	@Inject
	protected ChatMessageManager chatMessageManager;
	@Inject
	protected OverlayManager overlayManager;
	@Inject
	protected ClientThread clientThread;

	@Inject
	protected Room(TheatrePlugin plugin, TheatreConfig config)
	{
		this.plugin = plugin;
		this.config = config;
	}

	public void init()
	{
	}

	public void load()
	{
	}

	public void unload()
	{
	}

	public boolean inRoomRegion(Integer roomRegionId)
	{
		return ArrayUtils.contains(client.getMapRegions(), roomRegionId);
	}

	public int getTobRaidStatus() {
		return client.getVar(Varbits.THEATRE_OF_BLOOD);
	}

	public int getTobRoomStatus() {
		return client.getVarbitValue(6447);
	}

	public int getInstanceRegionId() {
		return WorldPoint.fromLocalInstance(this.client, Objects.requireNonNull(this.client.getLocalPlayer()).getLocalLocation()).getRegionID();
	}

	public boolean isNpcFromName(@Nullable NPC npc, @Nonnull String name) {
		return npc != null && !Strings.isNullOrEmpty(npc.getName()) && npc.getName().equalsIgnoreCase(name);
	}

	public void addIfTrue(@Nonnull Object obj, @Nonnull Supplier<Boolean> state) {
		if ((Boolean)state.get()) {
			Preconditions.checkArgument(obj instanceof Overlay, "Object was not an instanceof Overlay");
			this.overlayManager.add((Overlay)obj);
		}
	}

	public void addIfTrueRemoveIfFalse(@Nonnull Class<? extends Overlay> clazz, @Nonnull Object obj, @Nonnull Supplier<Boolean> state) {
		if (!(Boolean)state.get()) {
			OverlayManager var10000 = this.overlayManager;
			Objects.requireNonNull(clazz);
			var10000.removeIf(clazz::isInstance);
		} else {
			Preconditions.checkArgument(obj instanceof Overlay, "Object was not an instanceof Overlay");
			this.overlayManager.add((Overlay)obj);
		}
	}

	public void removeGameObjectsFromScene(int plane, int... gameObjectIDs) {
		Scene scene = this.client.getScene();
		Tile[][] tiles = scene.getTiles()[plane];

		for(int x = 0; x < 104; ++x) {
			for(int y = 0; y < 104; ++y) {
				Tile tile = tiles[x][y];
				if (tile != null) {
					GameObject[] var8 = tile.getGameObjects();

					for (GameObject gameObject : var8) {
						if (gameObject != null) {

							for (int id : gameObjectIDs) {
								if (id == gameObject.getId()) {
									scene.removeGameObject(gameObject);
									break;
								}
							}
						}
					}
				}
			}
		}

	}
}

