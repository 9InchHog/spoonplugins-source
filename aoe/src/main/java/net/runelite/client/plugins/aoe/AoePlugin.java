package net.runelite.client.plugins.aoe;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;

//import static net.runelite.client.plugins.projectilewarnings.AoeConfig.OlmMode;

@Extension
@PluginDescriptor(
	name = "<html><font color=#25c550>[S] AoE Warnings",
	enabledByDefault = false,
	description = "Shows the final destination for AoE Attack projectiles",
	tags = {"bosses", "combat", "pve", "overlay"}
)
public class AoePlugin extends Plugin
{
	@Getter(AccessLevel.PACKAGE)
	private final Set<CrystalBomb> bombs = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private final Set<ProjectileContainer> projectiles = new HashSet<>();

	@Inject
	public AoeConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AoeOverlay coreOverlay;

	@Inject
	private BombOverlay bombOverlay;

	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	private List<WorldPoint> lightningTrail = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private List<GameObject> acidTrail = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private List<GameObject> crystalSpike = new ArrayList<>();

	@Getter(AccessLevel.PACKAGE)
	private List<GameObject> wintertodtSnowFall = new ArrayList<>();

	private static final int VERZIK_REGION = 12611;
	private static final int GROTESQUE_GUARDIANS_REGION = 6727;

	public ArrayList<Color> raveColors = new ArrayList<Color>();

	public Color flowColor = new Color(75, 25, 150, 255);
	private boolean raveRedUp = true;
	private boolean raveGreenUp = true;
	private boolean raveBlueUp = true;

	private boolean mirrorMode;

	@Provides
	AoeConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AoeConfig.class);
	}

	@Override
	protected void startUp() {
		overlayManager.add(coreOverlay);
		overlayManager.add(bombOverlay);
		reset();
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(coreOverlay);
		overlayManager.remove(bombOverlay);
		reset();
	}

	@Subscribe
	private void onProjectileSpawned(ProjectileSpawned event) {
		final Projectile projectile = event.getProjectile();

		if (AoeProjectileInfo.getById(projectile.getId()) != null) {
			final int id = projectile.getId();
			final int lifetime = config.delay() + (projectile.getRemainingCycles() * 20);
			int ticksRemaining = projectile.getRemainingCycles() / 30;
			if (!isTickTimersEnabledForProjectileID(id)) {
				ticksRemaining = 0;
			}
			final int tickCycle = client.getTickCount() + ticksRemaining;
			if (isConfigEnabledForProjectileId(id)) {
				projectiles.add(new ProjectileContainer(projectile, Instant.now(), lifetime, tickCycle));
			}
		}
	}

	@Subscribe
	private void onProjectileMoved(ProjectileMoved event) {
		if (!projectiles.isEmpty()) {
			final Projectile projectile = event.getProjectile();
			projectiles.forEach(proj -> {
				if (proj.getProjectile() == projectile) {
					proj.setTargetPoint(event.getPosition());
				}
			});
		}
	}

	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned event) {
		final GameObject gameObject = event.getGameObject();

		switch (gameObject.getId()) {
			case ObjectID.CRYSTAL_BOMB:
				bombs.add(new CrystalBomb(gameObject, client.getTickCount()));
				break;
			case ObjectID.ACID_POOL:
				acidTrail.add(gameObject);
				break;
			case ObjectID.SMALL_CRYSTALS:
				crystalSpike.add(gameObject);
				break;
			case NullObjectID.NULL_26690:
				if (config.isWintertodtEnabled()) {
					wintertodtSnowFall.add(gameObject);
				}
				break;
		}
	}

	@Subscribe
	private void onGameObjectDespawned(GameObjectDespawned event) {
		final GameObject gameObject = event.getGameObject();

		switch (gameObject.getId()) {
			case ObjectID.CRYSTAL_BOMB:
				bombs.removeIf(o -> o.getGameObject() == gameObject);
				break;
			case ObjectID.ACID_POOL:
				acidTrail.remove(gameObject);
				break;
			case ObjectID.SMALL_CRYSTALS:
				crystalSpike.remove(gameObject);
				break;
			case NullObjectID.NULL_26690:
				wintertodtSnowFall.remove(gameObject);
				break;
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			return;
		}
		reset();
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		lightningTrail.clear();

		if (config.LightningTrail()) {
			client.getGraphicsObjects().forEach(o -> {
				if (o.getId() == GraphicID.OLM_LIGHTNING)
				{
					lightningTrail.add(WorldPoint.fromLocal(client, o.getLocation()));
				}
			});
		}
		bombs.forEach(CrystalBomb::bombClockUpdate);

		raveColors.clear();
		int highlightNum = lightningTrail.size() + projectiles.size() + acidTrail.size() + wintertodtSnowFall.size();
		for(int i=0; i<highlightNum; i++){
			raveColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
		}
	}

	private boolean isTickTimersEnabledForProjectileID(int projectileId) {
		AoeProjectileInfo projectileInfo = AoeProjectileInfo.getById(projectileId);

		if (projectileInfo == null) {
			return false;
		}

		switch (projectileInfo) {
			case VASA_RANGED_AOE:
			case VORKATH_POISON_POOL:
			case VORKATH_SPAWN:
			case VORKATH_TICK_FIRE:
			case OLM_BURNING:
			case OLM_FALLING_CRYSTAL_TRAIL:
			case OLM_ACID_TRAIL:
			case OLM_FIRE_LINE:
				return false;
		}

		return true;
	}

	private boolean isConfigEnabledForProjectileId(int projectileId) {
		AoeProjectileInfo projectileInfo = AoeProjectileInfo.getById(projectileId);
		if (projectileInfo == null) {
			return false;
		}

		switch (projectileInfo) {
			case LIZARDMAN_SHAMAN_AOE:
				return config.isShamansEnabled();
			case CRAZY_ARCHAEOLOGIST_AOE:
				return config.isArchaeologistEnabled();
			case ICE_DEMON_RANGED_AOE:
			case ICE_DEMON_ICE_BARRAGE_AOE:
				return config.isIceDemonEnabled();
			case VASA_AWAKEN_AOE:
			case VASA_RANGED_AOE:
				return config.isVasaEnabled();
			case TEKTON_METEOR_AOE:
				return config.isTektonEnabled();
			case VETION_LIGHTNING:
				return config.isVetionEnabled();
			case CHAOS_FANATIC:
				return config.isChaosFanaticEnabled();
			case GALVEK_BOMB:
			case GALVEK_MINE:
				return config.isGalvekEnabled();
			case DAWN_FREEZE:
			case DUSK_CEILING:
				if (regionCheck(GROTESQUE_GUARDIANS_REGION)) {
					return config.isGargBossEnabled();
				}
			case VERZIK_HM:
				if (regionCheck(VERZIK_REGION)) {
					return config.isVerzikHMEnabled();
				}
			case OLM_FALLING_CRYSTAL:
				return config.olmFallingCrystal();
			case OLM_BURNING:
				return config.olmBurning();
			case OLM_FALLING_CRYSTAL_TRAIL:
				return config.olmCrystal();
			case OLM_ACID_TRAIL:
				return config.olmAcid();
			case OLM_FIRE_LINE:
				return config.olmFlameWall();
			case CORPOREAL_BEAST:
				return config.isCorpEnabled();
			case CORPOREAL_BEAST_DARK_CORE:
				return config.isDarkCoreEnabled();
			case WINTERTODT_SNOW_FALL:
				return config.isWintertodtEnabled();
			case XARPUS_POISON_AOE:
				return config.isXarpusEnabled();
			case ADDY_DRAG_POISON:
				return config.addyDrags();
			case DRAKE_BREATH:
				return config.isDrakeEnabled();
			case CERB_FIRE:
				return config.isCerbFireEnabled();
			case VERZIK_CRAB:
				return config.isVerzikPurpleEnabled();
			case VERZIK_P2_RANGE:
				return config.isVerzikRangeEnabled();
			case VORKATH_BOMB:
				return config.vorkathBombs();
			case VORKATH_POISON_POOL:
				return config.vorkathPools();
			case VORKATH_SPAWN:
				return config.vorkathSpawn();
			case VORKATH_TICK_FIRE:
				return config.vorkathRapid();
			case DEMONIC_GORILLA_BOULDER:
				return config.isDemonicGorillaEnabled();

		}
		return false;
	}

	private void reset()
	{
		lightningTrail.clear();
		acidTrail.clear();
		crystalSpike.clear();
		wintertodtSnowFall.clear();
		bombs.clear();
		projectiles.clear();
	}

	private boolean regionCheck(int region) {
		return ArrayUtils.contains(client.getMapRegions(), region);
	}

	@Subscribe
	private void onClientTick(ClientTick event) {
		/*if (client.isMirrored() && !mirrorMode) {
			coreOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
			overlayManager.remove(coreOverlay);
			overlayManager.add(coreOverlay);
			bombOverlay.setLayer(OverlayLayer.AFTER_MIRROR);
			overlayManager.remove(bombOverlay);
			overlayManager.add(bombOverlay);
			mirrorMode = true;
		}*/

		flowColor();
	}

	public void flowColor () {
		int red = flowColor.getRed();
		red += raveRedUp ? 1 : -1;
		if(red == 255 || red == 0) {
			raveRedUp = !raveRedUp;
		}
		int green = flowColor.getGreen();
		green += raveGreenUp ? 1 : -1;
		if(green == 255 || green == 0) {
			raveGreenUp = !raveGreenUp;
		}
		int blue = flowColor.getBlue();
		blue += raveBlueUp ? 1 : -1;
		if(blue == 255 || blue == 0) {
			raveBlueUp = !raveBlueUp;
		}
		flowColor = new Color(red, green, blue, 255);
	}
}