package net.runelite.client.plugins.spoonnpchighlight;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import static net.runelite.api.MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;

@Extension
@PluginDescriptor(
        name = "<html><font color=#25c550>[S] Npc Highlight",
        description = "NPC highlight for brainlets",
        tags = {"SpoonNpcHighlight", "spoon", "npc", "highlight", "indicators"},
        conflicts = "NPC Indicators"
)
public class SpoonNpcHighlightPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SpoonNpcHighlightOverlay overlay;

    @Inject
    private SpoonNpcHighlightConfig config;

    @Inject
    private SpoonNpcMinimapOverlay mapOverlay;

    @Inject
    private ConfigManager configManager;

    private static final Set<MenuAction> NPC_MENU_ACTIONS = ImmutableSet.of(MenuAction.NPC_FIRST_OPTION, MenuAction.NPC_SECOND_OPTION,
		MenuAction.NPC_THIRD_OPTION, MenuAction.NPC_FOURTH_OPTION, MenuAction.NPC_FIFTH_OPTION, MenuAction.SPELL_CAST_ON_NPC,
		MenuAction.ITEM_USE_ON_NPC);

    public ArrayList<String> tileNames = new ArrayList<String>();
    public ArrayList<Integer> tileIds = new ArrayList<Integer>();
    public ArrayList<String> trueTileNames = new ArrayList<String>();
    public ArrayList<Integer> trueTileIds = new ArrayList<Integer>();
    public ArrayList<String> swTileNames = new ArrayList<String>();
    public ArrayList<Integer> swTileIds = new ArrayList<Integer>();
    public ArrayList<String> hullNames = new ArrayList<String>();
    public ArrayList<Integer> hullIds = new ArrayList<Integer>();
    public ArrayList<String> areaNames = new ArrayList<String>();
    public ArrayList<Integer> areaIds = new ArrayList<Integer>();
    public ArrayList<String> outlineNames = new ArrayList<String>();
    public ArrayList<Integer> outlineIds = new ArrayList<Integer>();
    public ArrayList<String> turboNames = new ArrayList<String>();
    public ArrayList<Integer> turboIds = new ArrayList<Integer>();
    public ArrayList<Color> turboColors = new ArrayList<Color>();
    public ArrayList<NpcSpawn> npcSpawns = new ArrayList<NpcSpawn>();
    public ArrayList<String> namesToDisplay = new ArrayList<String>();
    public ArrayList<String> ignoreDeadExclusionList = new ArrayList<String>();
    public Instant lastTickUpdate;
    public int turboModeStyle = 0;
    public int turboTileWidth = 0;
    public int turboOutlineWidth = 0;
    public int turboOutlineFeather = 0;

    @Provides
    SpoonNpcHighlightConfig providesConfig(ConfigManager configManager) {
        return (SpoonNpcHighlightConfig)configManager.getConfig(SpoonNpcHighlightConfig.class);
    }

    protected void startUp() {
        reset();
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.mapOverlay);
        splitNameList(config.tileNames(), tileNames);
        splitIdList(config.tileIds(), tileIds);
        splitNameList(config.trueTileNames(), trueTileNames);
        splitIdList(config.trueTileIds(), trueTileIds);
        splitNameList(config.swTileNames(), swTileNames);
        splitIdList(config.swTileIds(), swTileIds);
        splitNameList(config.hullNames(), hullNames);
        splitIdList(config.hullIds(), hullIds);
        splitNameList(config.areaNames(), areaNames);
        splitIdList(config.areaIds(), areaIds);
        splitNameList(config.outlineNames(), outlineNames);
        splitIdList(config.outlineIds(), outlineIds);
        splitNameList(config.turboNames(), turboNames);
        splitIdList(config.turboIds(), turboIds);
        splitNameList(config.displayName(), namesToDisplay);
        splitNameList(config.ignoreDeadExclusion(), ignoreDeadExclusionList);
        parseBlueliteNpcConfig();
    }

    protected void shutDown() {
        reset();
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.mapOverlay);
    }

    private void reset() {
        tileNames.clear();
        tileIds.clear();
        trueTileNames.clear();
        trueTileIds.clear();
        swTileNames.clear();
        swTileIds.clear();
        hullNames.clear();
        hullIds.clear();
        areaNames.clear();
        areaIds.clear();
        outlineNames.clear();
        outlineIds.clear();
        npcSpawns.clear();
        turboModeStyle = 0;
        turboTileWidth = 0;
        turboOutlineWidth = 0;
        turboOutlineFeather = 0;
        namesToDisplay.clear();
        ignoreDeadExclusionList.clear();
    }

    private void splitNameList(String configStr, ArrayList<String> strList) {
        if(!configStr.equals("")){
            for(String str : configStr.split(",")){
                if(!str.trim().equals("")){
                   strList.add(str.trim().toLowerCase());
                }
            }
        }
    }

    private void splitIdList(String configStr, ArrayList<Integer> idList) {
        if(!configStr.equals("")){
            for(String str : configStr.split(",")){
                if(!str.trim().equals("")){
                    try {
                        idList.add(Integer.parseInt(str.trim()));
                    } catch (Exception ex) {
                        System.out.println("npc Highlight: " + ex.getMessage());
                    }
                }
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {        
        if (event.getKey().equals("tileNames")) {
            tileNames.clear();
            splitNameList(config.tileNames(), tileNames);
        } else if (event.getKey().equals("tileIds")) {
            tileIds.clear();
            splitIdList(config.tileIds(), tileIds);
        } else if (event.getKey().equals("trueTileNames")) {
            trueTileNames.clear();
            splitNameList(config.trueTileNames(), trueTileNames);
        } else if (event.getKey().equals("trueTileIds")) {
            trueTileIds.clear();
            splitIdList(config.trueTileIds(), trueTileIds);
        } else if (event.getKey().equals("swTileNames")) {
            swTileNames.clear();
            splitNameList(config.swTileNames(), swTileNames);
        } else if (event.getKey().equals("swTileIds")) {
            swTileIds.clear();
            splitIdList(config.swTileIds(), swTileIds);
        } else if (event.getKey().equals("hullNames")) {
            hullNames.clear();
            splitNameList(config.hullNames(), hullNames);
        } else if (event.getKey().equals("hullIds")) {
            hullIds.clear();
            splitIdList(config.hullIds(), hullIds);
        } else if (event.getKey().equals("areaNames")) {
            areaNames.clear();
            splitNameList(config.areaNames(), areaNames);
        } else if (event.getKey().equals("areaIds")) {
            areaIds.clear();
            splitIdList(config.areaIds(), areaIds);
        } else if (event.getKey().equals("outlineNames")) {
            outlineNames.clear();
            splitNameList(config.outlineNames(), outlineNames);
        } else if (event.getKey().equals("outlineIds")) {
            outlineIds.clear();
            splitIdList(config.outlineIds(), outlineIds);
        } else if (event.getKey().equals("turboNames")) {
            turboNames.clear();
            splitNameList(config.turboNames(), turboNames);
        } else if (event.getKey().equals("turboIds")) {
            turboIds.clear();
            splitIdList(config.turboIds(), turboIds);
        } else if (event.getKey().equals("displayName")) {
            namesToDisplay.clear();
            splitNameList(config.displayName(), namesToDisplay);
        } else if (event.getKey().equals("ignoreDeadExclusion")) {
            ignoreDeadExclusionList.clear();
            splitNameList(config.ignoreDeadExclusion(), ignoreDeadExclusionList);
        }
    }

    @Subscribe
	public void onGameStateChanged(GameStateChanged event){
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING){
			npcSpawns.clear();
		}
	}

    @Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event){
        int type = event.getType();
        if (type >= MENU_ACTION_DEPRIORITIZE_OFFSET) {
            type -= MENU_ACTION_DEPRIORITIZE_OFFSET;
        }
        final MenuAction menuAction = MenuAction.of(type);
		if (NPC_MENU_ACTIONS.contains(menuAction)) {
			NPC npc = client.getCachedNPCs()[event.getIdentifier()];

            Color color = null;
            if (npc.isDead()) {
                color = config.deadNpcMenuColor();
            } else if(config.highlightMenuNames() && npc.getName() != null && checkAllLists(npc)){
                color = config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TURBO ? Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F) : config.highlightColor();
            }

            if (color != null) {
                MenuEntry[] menuEntries = client.getMenuEntries();
                final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
                final String target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), color);
                menuEntry.setTarget(target);
                client.setMenuEntries(menuEntries);
            }
		} else if (menuAction == MenuAction.EXAMINE_NPC) {
			final int id = event.getIdentifier();
			final NPC npc = client.getCachedNPCs()[id];

			if (npc != null){
                String option;
                if (npc.getName() != null) {
                    if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TILE) {
                        if (tileNames.contains(npc.getName().toLowerCase())){
                            option = "Untag-Tile";
                        } else{
                            option = "Tag-Tile";
                        }
                    } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TRUE_TILE) {
                        if (trueTileNames.contains(npc.getName().toLowerCase())){
                            option = "Untag-True-Tile";
                        } else{
                            option = "Tag-True-Tile";
                        }
                    } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.SW_TILE) {
                        if (swTileNames.contains(npc.getName().toLowerCase())){
                            option = "Untag-SW-Tile";
                        } else{
                            option = "Tag-SW-Tile";
                        }
                    } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.HULL) {
                        if (hullNames.contains(npc.getName().toLowerCase())){
                            option = "Untag-Hull";
                        } else{
                            option = "Tag-Hull";
                        }
                    } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.AREA) {
                        if(areaNames.contains(npc.getName().toLowerCase())){
                            option = "Untag-Area";
                        }else{
                            option = "Tag-Area";
                        }
                    } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.OUTLINE) {
                        if(outlineNames.contains(npc.getName().toLowerCase())){
                            option = "Untag-Outline";
                        }else{
                            option = "Tag-Outline";
                        }
                    } else {
                        if(turboNames.contains(npc.getName().toLowerCase())){
                            option = "Untag-Turbo";
                        }else{
                            option = "Tag-Turbo";
                        }
                    }

                    if (option.contains("Untag-") && (config.highlightMenuNames() || (npc.isDead() && config.deadNpcMenuColor() != null))) {
                        MenuEntry[] menuEntries = client.getMenuEntries();
                        final MenuEntry menuEntry = menuEntries[menuEntries.length - 1];
                        String target;
                        if (option.contains("Turbo")) {
                            target = ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
                         }else {
                            target = npc.isDead() ? ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), config.deadNpcMenuColor()) : ColorUtil.prependColorTag(Text.removeTags(event.getTarget()), config.highlightColor());
                        }
                        menuEntry.setTarget(target);
                        client.setMenuEntries(menuEntries);
                    }

                    if(client.isKeyPressed(KeyCode.KC_SHIFT)) {
                        String tagAllEntry = "";
                        if (config.highlightMenuNames() || (npc.isDead() && config.deadNpcMenuColor() != null)) {
                            String colorCode;
                            if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TURBO) {
                                if (turboColors.size() == 0 && turboNames.contains(npc.getName().toLowerCase())) {
                                    colorCode = Integer.toHexString(turboColors.get(turboNames.indexOf(npc.getName().toLowerCase())).getRGB());
                                } else {
                                    colorCode = Integer.toHexString(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F).getRGB());
                                }
                            } else {
                                colorCode = npc.isDead() ? Integer.toHexString(config.deadNpcMenuColor().getRGB()) : Integer.toHexString(config.highlightColor().getRGB());
                            }
                            tagAllEntry = "<col=" + colorCode.substring(2) + ">" + Text.removeTags(event.getTarget());
                        } else {
                            tagAllEntry = event.getTarget();
                        }

                        client.createMenuEntry(-1)
                                .setOption(option)
                                .setTarget(tagAllEntry)
                                .setIdentifier(event.getIdentifier())
                                .setParam0(event.getActionParam0())
                                .setParam1(event.getActionParam1())
                                .setType(MenuAction.RUNELITE);
                    }
                }
			}
		}
	}

    @Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (event.getMenuAction() == MenuAction.RUNELITE) {
            if((event.getMenuOption().contains("Tag") || event.getMenuOption().contains("Untag")) && (event.getMenuOption().contains("-Tile")
                    || event.getMenuOption().contains("-True-Tile") || event.getMenuOption().contains("-SW-Tile") || event.getMenuOption().contains("-Hull")
                    || event.getMenuOption().contains("-Area") || event.getMenuOption().contains("-Outline") || event.getMenuOption().contains("-Turbo"))){
                final int id = event.getId();
	            final NPC[] cachedNPCs = client.getCachedNPCs();
	            final NPC npc = cachedNPCs[id];

	            ArrayList<String> listToChange = new ArrayList<>();
	            if(npc.getName() != null) {
                    if (event.getMenuOption().contains("Untag")) {
                        if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TILE) {
                            tileNames.remove(npc.getName().toLowerCase());
                            listToChange = tileNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TRUE_TILE) {
                            trueTileNames.remove(npc.getName().toLowerCase());
                            listToChange = trueTileNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.SW_TILE) {
                            swTileNames.remove(npc.getName().toLowerCase());
                            listToChange = swTileNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.HULL) {
                            hullNames.remove(npc.getName().toLowerCase());
                            listToChange = hullNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.AREA) {
                            areaNames.remove(npc.getName().toLowerCase());
                            listToChange = areaNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.OUTLINE) {
                            outlineNames.remove(npc.getName().toLowerCase());
                            listToChange = outlineNames;
                        }else {
                            turboNames.remove(npc.getName().toLowerCase());
                            listToChange = turboNames;
                        }
                    } else {
                        if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TILE) {
                            tileNames.add(npc.getName());
                            listToChange = tileNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TRUE_TILE) {
                            trueTileNames.add(npc.getName());
                            listToChange = trueTileNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.SW_TILE) {
                            swTileNames.add(npc.getName());
                            listToChange = swTileNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.HULL) {
                            hullNames.add(npc.getName());
                            listToChange = hullNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.AREA) {
                            areaNames.add(npc.getName());
                            listToChange = areaNames;
                        } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.OUTLINE) {
                            outlineNames.add(npc.getName());
                            listToChange = outlineNames;
                        }else {
                            turboNames.add(npc.getName());
                            listToChange = turboNames;
                        }
                    }
                }

                if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TILE) {
                    config.setTileNames(Text.toCSV(listToChange));
                } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.TRUE_TILE) {
                    config.setTrueTileNames(Text.toCSV(listToChange));
                } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.SW_TILE) {
                    config.setSwTileNames(Text.toCSV(listToChange));
                } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.HULL) {
                    config.setHullNames(Text.toCSV(listToChange));
                } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.AREA) {
                    config.setAreaNames(Text.toCSV(listToChange));
                } else if (config.tagStyleMode() == SpoonNpcHighlightConfig.tagStyleMode.OUTLINE) {
                    config.setOutlineNames(Text.toCSV(listToChange));
                }else {
                    config.setTurboNames(Text.toCSV(listToChange));
                }
	            event.consume();
            }
		}
	}

    @Subscribe
	public void onNpcSpawned(NpcSpawned event){
        for(NpcSpawn n : npcSpawns){
            if(event.getNpc().getIndex() == n.index){
                if(n.spawnPoint == null) {
                    NPCComposition comp = event.getNpc().getTransformedComposition();
                    if (comp != null) {
                        for (WorldPoint wp : n.spawnLocations) {
                            if (wp.getX() == event.getNpc().getWorldLocation().getX() && wp.getY() == event.getNpc().getWorldLocation().getY()) {
                                n.spawnPoint = event.getNpc().getWorldLocation();
                                n.respawnTime = client.getTickCount() - n.diedOnTick + 1;
                                break;
                            }
                        }
                    }
                    n.spawnLocations.add(event.getNpc().getWorldLocation());
                }
                n.dead = false;
                break;
            }
        }
    }

    @Subscribe
	public void onNpcDespawned(NpcDespawned event){
        if(event.getNpc().isDead()){
            for(NpcSpawn n : npcSpawns){
                if(event.getNpc().getIndex() == n.index){
                    n.diedOnTick = client.getTickCount();
                    n.dead = true;
                    return;
                }
            }

            if (checkAllLists(event.getNpc())) {
                npcSpawns.add(new NpcSpawn(event.getNpc()));
            }
        }
    }

    @Subscribe
	public void onGameTick(GameTick event){
        lastTickUpdate = Instant.now();
        turboColors.clear();
        for(int i=0; i<turboNames.size() + turboIds.size(); i++){
            turboColors.add(Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F));
        }
        turboModeStyle = new Random().nextInt(6);
        turboTileWidth = new Random().nextInt(10) + 1;
        turboOutlineWidth = new Random().nextInt(50) + 1;
        turboOutlineFeather = new Random().nextInt(4);
    }

    private void parseBlueliteNpcConfig() {
        if (config.tileNames().trim().isEmpty() && configManager.getConfiguration("highlightnpcs", "npcToHighlight") != null)
        {
            configManager.setConfiguration("SpoonNpcHighlight", "tileNames", configManager.getConfiguration("highlightnpcs", "npcToHighlight"));
        }
        if (config.tileIds().trim().isEmpty() && configManager.getConfiguration("highlightnpcs", "idToHighlight") != null)
        {
            configManager.setConfiguration("SpoonNpcHighlight", "tileIds", configManager.getConfiguration("highlightnpcs", "idToHighlight"));
        }
    }

    public boolean checkSpecificList(ArrayList<String> strList, ArrayList<Integer> intList, NPC npc) {
        if (intList.contains(npc.getId())) {
            return true;
        } else if (npc.getName() != null) {
            String name = npc.getName().toLowerCase();
            for(String str : strList){
                if(str.equalsIgnoreCase(name) || (str.contains("*")
                        && ((str.startsWith("*") && str.endsWith("*") && name.contains(str.replace("*", "")))
                        || (str.startsWith("*") && name.endsWith(str.replace("*", ""))) || name.startsWith(str.replace("*", ""))))){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkAllLists(NPC npc) {
        int id = npc.getId();
        if (tileIds.contains(id) || trueTileIds.contains(id) || swTileIds.contains(id) || hullIds.contains(id) || areaIds.contains(id) || outlineIds.contains(id) || turboIds.contains(id)) {
            return true;
        } else if (npc.getName() != null) {
            String name = npc.getName().toLowerCase();
            for(ArrayList<String> strList : new ArrayList<>(Arrays.asList(tileNames, trueTileNames, swTileNames, hullNames, areaNames, outlineNames, turboNames))){
                for(String str : strList){
                    if(str.equalsIgnoreCase(name) || (str.contains("*")
                            && ((str.startsWith("*") && str.endsWith("*") && name.contains(str.replace("*", "")))
                            || (str.startsWith("*") && name.endsWith(str.replace("*", ""))) || name.startsWith(str.replace("*", ""))))){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
