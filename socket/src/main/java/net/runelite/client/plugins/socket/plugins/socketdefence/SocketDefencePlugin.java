package net.runelite.client.plugins.socket.plugins.socketdefence;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketMembersUpdate;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.plugins.socket.packet.SocketShutdown;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static net.runelite.api.ScriptID.XPDROPS_SETDROPSIZE;
import static net.runelite.api.widgets.WidgetInfo.TO_CHILD;
import static net.runelite.api.widgets.WidgetInfo.TO_GROUP;

@PluginDescriptor(
        name = "Socket - Defence",
        description = "Shows defence level for different bosses after specs",
        tags = {"socket", "pvm", "cox", "gwd", "corp", "tob"},
        enabledByDefault = false
)

@Slf4j
public class SocketDefencePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EventBus eventBus;

    @Inject
    private SkillIconManager skillIconManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private SocketDefenceConfig config;

    @Inject
    private SocketDefenceOverlay overlay;

    @Inject
    ConfigManager configManager;

    @Inject
    PluginManager pluginManager;

    public ArrayList<String> socketPlayerNames = new ArrayList<>();
    public String specWep = "";
    public String boss = "";
    public double bossDef = -1;
    public DefenceInfoBox box = null;
    private VulnerabilityInfoBox vulnBox = null;
    public SpritePixels vuln = null;
    public boolean vulnHit;
    public boolean isInCm = false;
    public ArrayList<String> bossList = new ArrayList<>(Arrays.asList("Corporeal Beast", "General Graardor", "K'ril Tsutsaroth", "Kalphite Queen", "The Maiden of Sugadinti",
            "Xarpus", "Great Olm (Left claw)", "Tekton", "Tekton (enraged)", "Callisto"));
    public boolean hmXarpus = false;

    private boolean mirrorMode;


    public SocketDefencePlugin() {
    }

    protected void startUp() throws Exception {
        reset();
        overlayManager.add(overlay);
    }

    protected void shutDown() throws Exception {
        reset();
        overlayManager.remove(overlay);
    }

    protected void reset() {
        infoBoxManager.removeInfoBox(box);
        infoBoxManager.removeInfoBox(vulnBox);
        boss = "";
        bossDef = -1;
        specWep = "";
        box = null;
        vulnBox = null;
        vuln = null;
        vulnHit = false;
        isInCm = config.cm();
    }

    @Provides
    SocketDefenceConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SocketDefenceConfig.class);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired)
    {
        if(!(specWep.contains("bgs") || specWep.contains("dwh")) )
            return;

        if (scriptPreFired.getScriptId() == XPDROPS_SETDROPSIZE)
        {
            final int[] intStack = client.getIntStack();
            final int intStackSize = client.getIntStackSize();
            final int widgetId = intStack[intStackSize - 4];
            try
            {
                processXpDrop(widgetId);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void processXpDrop(int widgetId) throws InterruptedException
    {
        final Widget xpdrop = client.getWidget(TO_GROUP(widgetId), TO_CHILD(widgetId));
        if(xpdrop == null) return;
        final Widget[] children = xpdrop.getChildren();
        final Widget textWidget = children[0];
        String text = textWidget.getText();
        boolean isDamage = false;
        Actor interacted = Objects.requireNonNull(client.getLocalPlayer()).getInteracting();
        String targetName = interacted.getName();

        if(!(targetName.contains("Maiden") || targetName.contains("Sotetseg") || targetName.contains("Xarpus")))
            return;

        Optional<Plugin> o = pluginManager.getPlugins().stream().filter(p->p.getName().equals("damagedrops")).findAny();
        if(o.isPresent())
        {
            if(pluginManager.isPluginEnabled(o.get()))
            {
                isDamage = (configManager.getConfiguration("damagedrops", "replaceEXPDrop").equals("true"));
            }
        }
        if(text.contains("<"))
        {
            if(text.contains("<img=11>"))
            {
                text = text.substring(9);
            }
            if(text.contains("<"))
            {
                text = text.substring(0, text.indexOf("<"));
            }
        }
        int damage = -1;
        int weaponUsed = Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition().getEquipmentId(KitType.WEAPON);

        //MELEE
        if(Arrays.stream(children).skip(1).filter(Objects::nonNull).mapToInt(Widget::getSpriteId).anyMatch(id->id== SpriteID.SKILL_ATTACK || id == SpriteID.SKILL_STRENGTH
                || id == SpriteID.SKILL_DEFENCE)) {
            if(client.getVarbitValue(4696) == 0) //Separate XP Drops
            {
                if(client.getVarbitValue(4696) == 0) //If split? xp drops, and weapon is mage,
                {
                    if (client.getVar(VarPlayer.ATTACK_STYLE) == 3) {
                        if (isDamage) {
                            damage = (int) (Integer.parseInt(text));
                        } else {
                            damage = Integer.parseInt(text);    //this is where split exp drops calcs a defensive sang
                        }
                    }
                } else    //combined
                {
                    if(isDamage)
                    {
                        damage = (int) (Integer.parseInt(text));
                    }
                    else
                    {
                        damage = Integer.parseInt(text) / 4;
                    }
                }
            }

            //combined exp drops
            else
            {

                if(isDamage)
                {
                    damage = Integer.parseInt(text);
                }
                else
                {
                    damage = (int) Math.round(Integer.parseInt(text) / 5.3333);
                }
            }

            String name = client.getLocalPlayer().getInteracting().getName();
            if(name==null) return;

            JSONObject data = new JSONObject();
            data.put("boss", name);
            data.put("weapon", specWep);
            data.put("hit", damage);
            JSONObject payload = new JSONObject();
            payload.put("socketdefence", data);
            this.eventBus.post(new SocketBroadcastPacket(payload));
            specWep = "";
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() != null && this.client.getLocalPlayer() != null && event.getActor().getName() != null) {
            int animation = event.getActor().getAnimation();
            if (event.getActor().getName().equals(this.client.getLocalPlayer().getName())) {
                if (animation == 1378) {
                    specWep = "dwh";
                } else if (animation == 7642 || animation == 7643) {
                    specWep = "bgs";
                } else if(animation == 2890) {
                    specWep = "arclight";
                } else {
                    specWep = "";
                }
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        for (NPC n : this.client.getNpcs()) {
            if (n != null && n.getName() != null && n.getName().equals(this.boss) && (n.isDead() || n.getHealthRatio() == 0)) {
                JSONObject data = new JSONObject();
                data.put("boss", this.boss);
                data.put("player", this.client.getLocalPlayer().getName());
                JSONObject payload = new JSONObject();
                payload.put("socketdefencebossdead", data);
                this.eventBus.post(new SocketBroadcastPacket(payload));
                reset();
            }
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if(!(event.getActor() instanceof NPC))
            return;

        NPC npc = (NPC) event.getActor();

        //my addition
        if(npc.getName().contains("Maiden") || npc.getName().contains("Sotetseg") || npc.getName().contains("Xarpus"))
            return;


        if (!specWep.equals("") && event.getHitsplat().isMine() && event.getActor() instanceof NPC && event.getActor() != null && event.getActor().getName() != null && bossList.contains(event.getActor().getName())) {

            String name;
            if(event.getActor().getName().contains("Tekton")){
                name = "Tekton";
            }else{
                name = event.getActor().getName();
            }
            JSONObject data = new JSONObject();
            data.put("boss", name);
            data.put("weapon", specWep);
            data.put("hit", event.getHitsplat().getAmount());
            JSONObject payload = new JSONObject();
            payload.put("socketdefence", data);
            this.eventBus.post(new SocketBroadcastPacket(payload));
            specWep = "";
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (event.getNpc().getId() >= 10770 && event.getNpc().getId() <= 10772) {
            hmXarpus = true;
        } else {
            hmXarpus = false;
        }
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if(event.getActor() instanceof NPC && event.getActor().getName() != null && this.client.getLocalPlayer() != null) {
            if (event.getActor().getName().equals(boss) || (event.getActor().getName().contains("Tekton") && boss.equals("Tekton"))) {
                JSONObject data = new JSONObject();
                data.put("boss", boss);
                data.put("player", this.client.getLocalPlayer().getName());
                JSONObject payload = new JSONObject();
                payload.put("socketdefencebossdead", data);
                this.eventBus.post(new SocketBroadcastPacket(payload));
                reset();
            }
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event) {
        try {
            JSONObject payload = event.getPayload();
            if (payload.has("socketdefence")) {
                JSONObject data = payload.getJSONObject("socketdefence");
                String bossName = data.getString("boss");
                String weapon = data.getString("weapon");
                int hit = data.getInt("hit");

                if(((bossName.equals("Tekton") || bossName.contains("Great Olm")) && this.client.getVar(Varbits.IN_RAID) != 1) ||
                        ((bossName.contains("The Maiden of Sugadinti") || bossName.contains("Xarpus")) && this.client.getVar(Varbits.THEATRE_OF_BLOOD) != 2)){
                    return;
                }
                if (boss.equals("") || bossDef == -1 || !boss.equals(bossName)) {
                    if (bossName.equals("Corporeal Beast")) {
                        bossDef = 310;
                    } else if (bossName.equals("General Graardor")) {
                        bossDef = 250;
                    } else if (bossName.equals("K'ril Tsutsaroth")) {
                        bossDef = 270;
                    } else if (bossName.equals("Kalphite Queen")) {
                        bossDef = 300;
                    } else if (bossName.equals("Callisto")){
                        bossDef = 440;
                    } else if (bossName.equals("The Maiden of Sugadinti")) {
                        bossDef = 200;
                    } else if (bossName.equals("Xarpus")) {
                        if (hmXarpus){
                            bossDef = 200;
                        } else {
                            bossDef = 250;
                        }
                    } else if (bossName.equals("Great Olm (Left claw)")) {
                        bossDef = 175 * (1 + (.01 * (this.client.getVarbitValue(5424) - 1)));

                        if (isInCm) {
                            bossDef = bossDef * 1.5;
                        }
                    } else if (bossName.equals("Tekton")) {
                        bossDef = 205 * (1 + (.01 * (this.client.getVarbitValue(5424) - 1)));

                        if (isInCm) {
                            bossDef = bossDef * 1.2;
                        }
                    }
                    boss = bossName;
                }

                if (weapon.equals("dwh")) {
                    if(hit == 0){
                        if(client.getVar(Varbits.IN_RAID) == 1 && boss.equals("Tekton")) {
                            bossDef -= bossDef * .05;
                        }
                    }else {
                        bossDef -= bossDef * .30;
                    }
                }else if (weapon.equals("bgs")) {
                    if(hit == 0){
                        if(client.getVar(Varbits.IN_RAID) == 1 && boss.equals("Tekton")) {
                            bossDef -= 10;
                        }
                    }else {
                        if (boss.equals("Corporeal Beast")) {
                            bossDef -= hit * 2;
                        } else {
                            bossDef -= hit;
                        }
                    }
                } else if (weapon.equals("arclight") && hit > 0) {
                    if(boss.equals("K'ril Tsutsaroth")){
                        bossDef -= bossDef * .10;
                    }else{
                        bossDef -= bossDef * .05;
                    }
                } else if (weapon.equals("vuln")){
                    if (config.vulnerability()) {
                        infoBoxManager.removeInfoBox(vulnBox);
                        IndexDataBase sprite = this.client.getIndexSprites();
                        vuln = client.getSprites(sprite, 56, 0)[0];
                        vulnBox = new VulnerabilityInfoBox(this.vuln.toBufferedImage(), this);
                        vulnBox.setTooltip(ColorUtil.wrapWithColorTag(boss, Color.WHITE));
                        infoBoxManager.addInfoBox(this.vulnBox);
                    }
                    vulnHit = true;
                    bossDef -= bossDef * .1;
                }

                if(bossDef < -1){
                    bossDef = 0;
                }
                infoBoxManager.removeInfoBox(box);
                box = new DefenceInfoBox(skillIconManager.getSkillImage(Skill.DEFENCE), this, Math.round(bossDef), config);
                box.setTooltip(ColorUtil.wrapWithColorTag(boss, Color.WHITE));
                infoBoxManager.addInfoBox(box);
            } else if (payload.has("socketdefencebossdead")) {
                JSONObject data = payload.getJSONObject("socketdefencebossdead");
                if(this.client.getLocalPlayer() != null && !data.getString("player").equals(this.client.getLocalPlayer().getName())){
                    String bossName = data.getString("boss");
                    if (bossName.equals(boss)) {
                        reset();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe void onSocketMembersUpdate(SocketMembersUpdate event){
        socketPlayerNames.clear();
        socketPlayerNames.addAll(event.getMembers());
    }

    @Subscribe
    private void onSocketShutdown(SocketShutdown event){
        socketPlayerNames.clear();
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        if ((client.getVar(Varbits.IN_RAID) != 1 && (boss.equals("Tekton") || boss.equals("Great Olm (Left claw)")))
                || (boss.equals("The Maiden of Sugadinti") && getInstanceRegionId() != TobRegions.MAIDEN.getRegionId())
                || (boss.equals("Xarpus") && getInstanceRegionId() != TobRegions.XARPUS.getRegionId())) {
            reset();
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event) {
        //85 = splash
        if (event.getActor().getName() != null && event.getActor().getGraphic() == 169) {
            if (bossList.contains(event.getActor().getName())) {
                if (event.getActor().getName().contains("Tekton")){
                    boss = "Tekton";
                } else {
                    boss = event.getActor().getName();
                }
                JSONObject data = new JSONObject();
                data.put("boss", boss);
                data.put("weapon", "vuln");
                data.put("hit", 0);
                JSONObject payload = new JSONObject();
                payload.put("socketdefence", data);
                this.eventBus.post(new SocketBroadcastPacket(payload));
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged e) {
        isInCm = config.cm();
    }

    public int getInstanceRegionId() {
        return WorldPoint.fromLocalInstance(this.client, this.client.getLocalPlayer().getLocalLocation()).getRegionID();
    }

    public enum TobRegions {
        MAIDEN(12613),
        BLOAT(13125),
        NYLOCAS(13122),
        SOTETSEG(13123),
        SOTETSEG_MAZE(13379),
        XARPUS(12612),
        VERZIK(12611);

        private final int regionId;

        TobRegions(int regionId) {
            this.regionId = regionId;
        }

        public int getRegionId() {
            return this.regionId;
        }
    }

    /*@Subscribe
    private void onClientTick(ClientTick event)
    {
        if (client.isMirrored() && !mirrorMode) {
            overlay.setLayer(OverlayLayer.AFTER_MIRROR);
            overlayManager.remove(overlay);
            overlayManager.add(overlay);
            mirrorMode = true;
        }
    }*/

}