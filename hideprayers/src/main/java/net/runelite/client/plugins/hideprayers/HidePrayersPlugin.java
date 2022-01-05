package net.runelite.client.plugins.hideprayers;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.hideprayers.util.PrayerTabStates;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Extension
@PluginDescriptor(
	name = "<html><font color=#25c550>[S] Hide Prayers",
	enabledByDefault = false,
	description = "Hides/shows prayers <br> Created by: OPRS <br> Modified by: SpoonLite"
)
public class HidePrayersPlugin extends Plugin {
	private static final List<WidgetInfo> PRAYER_WIDGET_INFO_LIST = ImmutableList.of(
		WidgetInfo.PRAYER_THICK_SKIN, //0
		WidgetInfo.PRAYER_BURST_OF_STRENGTH, //1
		WidgetInfo.PRAYER_CLARITY_OF_THOUGHT, //2
		WidgetInfo.PRAYER_SHARP_EYE, //3
		WidgetInfo.PRAYER_MYSTIC_WILL, //4
		WidgetInfo.PRAYER_ROCK_SKIN, //5
		WidgetInfo.PRAYER_SUPERHUMAN_STRENGTH, //6
		WidgetInfo.PRAYER_IMPROVED_REFLEXES, //7
		WidgetInfo.PRAYER_RAPID_RESTORE, //8
		WidgetInfo.PRAYER_RAPID_HEAL, //9
		WidgetInfo.PRAYER_PROTECT_ITEM, //10
		WidgetInfo.PRAYER_HAWK_EYE, //11
		WidgetInfo.PRAYER_MYSTIC_LORE, //12
		WidgetInfo.PRAYER_STEEL_SKIN, //13
		WidgetInfo.PRAYER_ULTIMATE_STRENGTH, //14
		WidgetInfo.PRAYER_INCREDIBLE_REFLEXES, //15
		WidgetInfo.PRAYER_PROTECT_FROM_MAGIC, //16
		WidgetInfo.PRAYER_PROTECT_FROM_MISSILES, //17
		WidgetInfo.PRAYER_PROTECT_FROM_MELEE, //18
		WidgetInfo.PRAYER_EAGLE_EYE, //19
		WidgetInfo.PRAYER_MYSTIC_MIGHT, //20
		WidgetInfo.PRAYER_RETRIBUTION, //21
		WidgetInfo.PRAYER_REDEMPTION, //22
		WidgetInfo.PRAYER_SMITE, //23
		WidgetInfo.PRAYER_PRESERVE, //24
		WidgetInfo.PRAYER_CHIVALRY, //25
		WidgetInfo.PRAYER_PIETY,  //26
		WidgetInfo.PRAYER_RIGOUR, //27
		WidgetInfo.PRAYER_AUGURY //28
	);

	public List<String> prayerList = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private HidePrayersConfig config;

	@Provides
	HidePrayersConfig provideConfig(ConfigManager configManager){
		return configManager.getConfig(HidePrayersConfig.class);
	}

	@Override
	protected void startUp(){
		hidePrayers();
	}

	@Override
	protected void shutDown(){
		restorePrayers();
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event){
		if (event.getGameState() == GameState.LOGGED_IN){
			reallyHidePrayers();
			hidePrayers();
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("spoonprayerhider")) {
			hidePrayers();
		}
	}

	@Subscribe
	private void onWidgetLoaded(WidgetLoaded event){
		if (event.getGroupId() == WidgetID.PRAYER_GROUP_ID || event.getGroupId() == WidgetID.QUICK_PRAYERS_GROUP_ID){
			hidePrayers();
		}
	}

	private PrayerTabStates getPrayerTabState(){
		HashTable<WidgetNode> componentTable = client.getComponentTable();
		for (WidgetNode widgetNode : componentTable.getNodes()){
			if (widgetNode.getId() == WidgetID.PRAYER_GROUP_ID){
				return PrayerTabStates.PRAYERS;
			}else if (widgetNode.getId() == WidgetID.QUICK_PRAYERS_GROUP_ID) {
				return PrayerTabStates.QUICK_PRAYERS;
			}
		}
		return PrayerTabStates.NONE;
	}

	private void restorePrayers(){
		if (client.getGameState() == GameState.LOGGED_IN){
			PrayerTabStates prayerTabState = getPrayerTabState();
			if (prayerTabState == PrayerTabStates.PRAYERS){
				List<Widget> prayerWidgets = PRAYER_WIDGET_INFO_LIST.stream().map(client::getWidget).filter(Objects::nonNull).collect(Collectors.toList());
				if (prayerWidgets.size() == PRAYER_WIDGET_INFO_LIST.size()){
					for (Widget w : prayerWidgets){
						w.setHidden(false);
					}
				}
			}
		}
	}

	private void reallyHidePrayers(){
		if (client.getGameState() == GameState.LOGGED_IN){
			PrayerTabStates prayerTabState = getPrayerTabState();
			if (prayerTabState == PrayerTabStates.PRAYERS){
				List<Widget> prayerWidgets = PRAYER_WIDGET_INFO_LIST.stream().map(client::getWidget).filter(Objects::nonNull).collect(Collectors.toList());
				if (prayerWidgets.size() == PRAYER_WIDGET_INFO_LIST.size()){
					for (Widget w : prayerWidgets){
						w.setHidden(true);
					}
				}
			}
		}
	}

	private void hidePrayers(){
		if (client.getGameState() == GameState.LOGGED_IN && getPrayerTabState() == PrayerTabStates.PRAYERS){
			List<Widget> prayerWidgets = PRAYER_WIDGET_INFO_LIST.stream().map(client::getWidget).filter(Objects::nonNull).collect(Collectors.toList());
			if (prayerWidgets.size() == PRAYER_WIDGET_INFO_LIST.size()){
				reallyHidePrayers();
				if (!config.prayerList().equals("") && config.showindividualprayers()) {
					this.prayerList.clear();
					String[] strArr = config.prayerList().split(",");
					for (String str : strArr) {
						if (!str.trim().equals("")) {
							prayerList.add(str.trim().toLowerCase());
						}
					}

					Widget x = this.client.getWidget(35454980);
					if (x != null) {
						for (Widget y : x.getStaticChildren()) {
							if (prayerList.contains(Text.removeTags(y.getName().toLowerCase()))) {
								y.setHidden(false);
							}
						}
					}
				}else if (config.getarmadylprayers()) {
					switch (config.armadyl()){
						case DISABLED:
							break;
						case ARMADYL_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // eagle eye
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case ARMADYL_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(27).setHidden(false);    // Rigour
							break;
					}
				}else if (config.getbandosprayers()){
					switch (config.bandos()){
						case DISABLED:
							break;
						case BANDOS_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflex
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case BANDOS_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							break;
						case BANDOS_CHEAP_RANGE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // eagle eye
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case BANDOS_EXPENSIVE_RANGE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(27).setHidden(false);    // Rigour
							break;
						case BANDOS_MAGE_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(20).setHidden(false);    // mystic might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case BANDOS_MAGE_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(28).setHidden(false);    // Augury
							break;
					}
				}else if (config.getbarrowsprayers()) {
					switch (config.barrows()){
						case DISABLED:
							break;
						case BARROWS_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // eagle eye
							prayerWidgets.get(20).setHidden(false);    // mystic might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case BARROWS_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(27).setHidden(false);    // Rigour
							prayerWidgets.get(28).setHidden(false);    // Augury
							break;
					}
				}else if (config.getcerberusprayers()){
					switch (config.cerberus()){
						case DISABLED:
							break;
						case CERBERUS_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflex
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case CERBERUS_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							break;
						case CERBERUS_CHEAP_RANGE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // eagle eye
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case CERBERUS_EXPENSIVE_RANGE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(27).setHidden(false);    // Rigour
							break;
					}
				}else if (config.getsaradominprayers()){
					switch (config.saradomin()){
						case DISABLED:
							break;
						case SARDOMIN_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflex
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // eagle eye
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case SARADOMIN_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							prayerWidgets.get(27).setHidden(false);    // Rigour
							break;
					}
				}else if (config.getvorkathprayers()){
					switch (config.vorkath()){
						case DISABLED:
							break;
						case VORKATH_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(19).setHidden(false);    // eagle eye
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case VORKATH_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(27).setHidden(false);    // Rigour
							break;
						case VORKATH_DHL:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							break;
					}
				}else if (config.getzamorakprayers()){
					switch (config.zamorak()){
						case DISABLED:
							break;
						case ZAMORAK_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflex
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case ZAMORAK_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							break;
						case ZAMORAK_RANGE_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);	   // Eagle Eye
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case ZAMORAK_RANGE_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(27).setHidden(false);    // Rigour
							break;
					}
				}else if (config.getzulrahprayers()){
					switch (config.zulrah()){
						case DISABLED:
							break;
						case ZULRAH_CHEAP:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(19).setHidden(false);    // eagle eye
							prayerWidgets.get(20).setHidden(false);    // mystic might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case ZULRAH_EXPENSIVE:
							prayerWidgets.get(8).setHidden(false);    // Rapid Restore
							prayerWidgets.get(9).setHidden(false);    // Rapid Heal
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(27).setHidden(false);    // Rigour
							prayerWidgets.get(28).setHidden(false);    // Augury
							break;
					}
				}else if (config.getpvpprayers()){
					if (config.HideRapidHealRestore()){
						prayerWidgets.get(8).setHidden(true);    // Rapid Restore
						prayerWidgets.get(9).setHidden(true);    // Rapid Heal
					}else{
						prayerWidgets.get(8).setHidden(false);    // Rapid Restore
						prayerWidgets.get(9).setHidden(false);    // Rapid Heal
					}

					if (WorldType.isPvpWorld(client.getWorldType()) || client.getRealSkillLevel(Skill.PRAYER) <= 24){
						prayerWidgets.get(10).setHidden(true);    // Protect Item
					}else{
						prayerWidgets.get(10).setHidden(false);    // Protect Item
					}

					switch (config.pvpprayers()){
						case DISABLED:
							reallyHidePrayers();
							break;
						case PRAY1:
							prayerWidgets.get(0).setHidden(false);    // Thick Skin
							break;
						case PRAY13:
							prayerWidgets.get(0).setHidden(false);    // Thick Skin
							prayerWidgets.get(3).setHidden(false);    // Sharp Eye
							prayerWidgets.get(4).setHidden(false);    // Mystic Will
							prayerWidgets.get(5).setHidden(false);    // Rock Skin
							prayerWidgets.get(6).setHidden(false);    // Super Human Strength
							break;
						case PRAY16:
						case PRAY25:
							prayerWidgets.get(3).setHidden(false);    // Sharp Eye
							prayerWidgets.get(4).setHidden(false);    // Mystic Will
							prayerWidgets.get(5).setHidden(false);    // Rock Skin
							prayerWidgets.get(6).setHidden(false);    // Super Human Strength
							prayerWidgets.get(7).setHidden(false);    // Improved Reflexed
							break;
						case PRAY31:
							prayerWidgets.get(7).setHidden(false);    // Improved Reflexed
							prayerWidgets.get(11).setHidden(false);    // Hawk Eye
							prayerWidgets.get(12).setHidden(false);    // Mystic Lore
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							break;
						case PRAY43:
							prayerWidgets.get(11).setHidden(false);    // Hawk Eye
							prayerWidgets.get(12).setHidden(false);    // Mystic Lore
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflexes
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							break;
						case PRAY44:
							prayerWidgets.get(12).setHidden(false);    // Mystic Lore
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflexes
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // Eagle Eye
							break;
						case PRAY45:
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflexes
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // Eagle Eye
							prayerWidgets.get(20).setHidden(false);    // Mystic Might
							break;
						case PRAY52:
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflexes
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // Eagle Eye
							prayerWidgets.get(20).setHidden(false);    // Mystic Might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(23).setHidden(false);    // Smite
							break;
						case PRAY55:
							prayerWidgets.get(13).setHidden(false);    // Steel Skin
							prayerWidgets.get(14).setHidden(false);    // Ultimate Strength
							prayerWidgets.get(15).setHidden(false);    // Incredible Reflexes
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // Eagle Eye
							prayerWidgets.get(20).setHidden(false);    // Mystic Might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(23).setHidden(false);    // Smite
							prayerWidgets.get(24).setHidden(false);    // Preserve
							break;
						case PRAY60:
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // Eagle Eye
							prayerWidgets.get(20).setHidden(false);    // Mystic Might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(23).setHidden(false);    // Smite
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(25).setHidden(false);    // Chivalry
							break;
						case PRAY70:
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(19).setHidden(false);    // Eagle Eye
							prayerWidgets.get(20).setHidden(false);    // Mystic Might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(23).setHidden(false);    // Smite
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							break;
						case PRAY74:
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(20).setHidden(false);    // Mystic Might
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(23).setHidden(false);    // Smite
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							prayerWidgets.get(27).setHidden(false);    // Rigour
							break;
						case PRAY77:
							prayerWidgets.get(16).setHidden(false);    // Protect from Magic
							prayerWidgets.get(17).setHidden(false);    // Protect from Range
							prayerWidgets.get(18).setHidden(false);    // Protect from Melee
							prayerWidgets.get(22).setHidden(false);    // Redemption
							prayerWidgets.get(23).setHidden(false);    // Smite
							prayerWidgets.get(24).setHidden(false);    // Preserve
							prayerWidgets.get(26).setHidden(false);    // Piety
							prayerWidgets.get(27).setHidden(false);    // Rigour
							prayerWidgets.get(28).setHidden(false);    // Augury
							break;
					}
				}else{
					restorePrayers();
				}
			}
		}
	}
}