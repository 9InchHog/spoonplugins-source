package net.runelite.client.plugins.animationcooldown;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Extension
@PluginDescriptor(
        name = "[S] Animation Cooldown",
        description = "Displays the cooldown for entries you set",
        tags = {"weapon", "cooldown", "cd", "animation", "cooldown"},
        enabledByDefault = false
)
public class AnimationCooldownPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AnimationCooldownPlugin.class);
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AnimationCooldownOverlay overlay;
    @Inject
    private AnimationCooldownConfig config;
    private final ArrayListMultimap<Player, Pair<Integer, Integer>> playerListMultiMap = ArrayListMultimap.create();
    private static final Splitter SPLITTER = Splitter.on("\n").omitEmptyStrings().trimResults();
    private final Predicate<Pair<Integer, Integer>> playerHandlerPredicate = (predicate) -> {
        return predicate.getRight() <= this.config.maxLostTicks() * -1;
    };
    private final Consumer<Pair<Integer, Integer>> playerHandlerConsumer = (consumer) -> {
        consumer.setValue(consumer.getRight() - 1);
    };
    private final ArrayListMultimap<Integer, Pair<Integer, Integer>> weaponListMultiMap = ArrayListMultimap.create();
    private final ArrayListMultimap<Integer, Integer> animationListMultiMap = ArrayListMultimap.create();

    public boolean inRaid;

    public AnimationCooldownPlugin() {
    }

    @Provides
    AnimationCooldownConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AnimationCooldownConfig.class);
    }

    public void startUp() {
        this.overlayManager.add(this.overlay);
        this.weaponListMultiMap.clear();
        this.parseWeaponConfigToMap(this.config.weaponList(), this.weaponListMultiMap);
        this.animationListMultiMap.clear();
        this.parseAnimationConfigToMap(this.config.animationList(), this.animationListMultiMap);
    }

    public void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.playerListMultiMap.clear();
        inRaid = false;
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("animationcooldown"))
            switch (event.getKey()) {
                case "trackOtherPlayers":
                    this.playerListMultiMap.clear();
                    break;
                case "customWeaponList":
                    this.weaponListMultiMap.clear();
                    parseWeaponConfigToMap(this.config.weaponList(), this.weaponListMultiMap);
                    break;
                case "customAnimationList":
                    this.animationListMultiMap.clear();
                    parseAnimationConfigToMap(this.config.animationList(), this.animationListMultiMap);
                    break;
            }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.CONNECTION_LOST || event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING) {
            this.playerListMultiMap.clear();
        }

    }

    @Subscribe
    private void onGameTick(GameTick event) {
        this.implementBlowpipeFix();
        if (this.playerListMultiMap.size() > 0) {
            this.playerListMultiMap.values().removeIf(this.playerHandlerPredicate);
            this.playerListMultiMap.values().forEach(this.playerHandlerConsumer);
        }
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        inRaid = this.client.getVar(Varbits.IN_RAID) == 1;
    }

    public boolean enforceRegion() {
        return inRegion(12611, 12612, 12613, 12687, 13122, 13123, 13125, 13379);
    }

    public boolean inRegion(int... regions) {
        if (this.client.getMapRegions() != null)
            for (int i : this.client.getMapRegions()) {
                for (int j : regions) {
                    if (i == j)
                        return true;
                }
            }
        return false;
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() instanceof Player) {
            Player player = (Player) event.getActor();
            int animation = player.getAnimation();
            if (this.animationListMultiMap.containsKey(animation)) {
                this.animationListMultiMap.get(animation).forEach((ticks) -> {
                    this.updatePlayerListMap(player, animation, ticks + 1);
                });
            }

            PlayerComposition composition = Optional.ofNullable(player.getPlayerComposition()).orElse(null);
            Optional<Integer> weapon = Optional.ofNullable(composition != null ? composition.getEquipmentId(KitType.WEAPON) : null);
            if (!weapon.isEmpty()) {
                if (this.weaponListMultiMap.containsKey(weapon.get())) {
                    this.weaponListMultiMap.get(weapon.get()).stream().filter((pair) -> {
                        return pair.getLeft() == animation;
                    }).forEach((id) -> {
                        this.updatePlayerListMap(player, weapon.get(), id.getRight() + 1);
                    });
                }

            }
        }
    }

    private void updatePlayerListMap(Player player, int id, int ticks) {
        if (this.config.trackOtherPlayers() || player == this.client.getLocalPlayer()) {
            if (!this.playerListMultiMap.containsKey(player)) {
                this.playerListMultiMap.put(player, new MutablePair<>(id, ticks));
            } else {
                this.playerListMultiMap.get(player).replaceAll((pair) -> {
                    return new MutablePair<>(id, ticks);
                });
            }
        }

    }

    private void implementBlowpipeFix() {
        if (this.config.blowpipeFix()) {
            this.playerListMultiMap.forEach((player, pair) -> {
                if (player.getAnimation() == 5061 && player.getInteracting() != null && pair.getLeft() == 12926 && pair.getRight() == 1) {
                    pair.setValue(pair.getRight() + 2);
                }

            });
        }

    }

    private void parseWeaponConfigToMap(String config, ArrayListMultimap<Integer, Pair<Integer, Integer>> map) {
        List<String> strList = SPLITTER.splitToList(config);

        for (String str : strList) {
            String[] stringList = str.split(",");
            if (stringList.length > 2) {
                try {
                    map.put(Integer.valueOf(stringList[0].trim()), new MutablePair<>(Integer.valueOf(stringList[1].trim()), Integer.valueOf(stringList[2].trim())));
                } catch (NumberFormatException var8) {
                    log.warn("Invalid ID Input in /Animation Cooldown/Weapon List/ -> {}", var8.getMessage());
                }
            }
        }

    }

    private void parseAnimationConfigToMap(String config, ArrayListMultimap<Integer, Integer> map) {
        List<String> strList = SPLITTER.splitToList(config);

        for (String str : strList) {
            String[] stringList = str.split(",");
            if (stringList.length > 1) {
                try {
                    map.put(Integer.valueOf(stringList[0].trim()), Integer.valueOf(stringList[1].trim()));
                } catch (NumberFormatException var8) {
                    log.warn("Invalid ID Input in /Animation Cooldown/Animation List/ -> {}", var8.getMessage());
                }
            }
        }

    }

    public ArrayListMultimap<Player, Pair<Integer, Integer>> getPlayerListMultiMap() {
        return this.playerListMultiMap;
    }
}
