package net.runelite.client.plugins.socket.plugins.deathindicators;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Provides;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.inject.Inject;

import com.openosrs.client.util.WeaponMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.socket.SocketPlugin;
import net.runelite.client.plugins.socket.org.json.JSONArray;
import net.runelite.client.plugins.socket.org.json.JSONObject;
import net.runelite.client.plugins.socket.packet.SocketBroadcastPacket;
import net.runelite.client.plugins.socket.packet.SocketReceivePacket;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;


@Slf4j
@Extension
@PluginDescriptor(
        name = "Socket - Death Indicators",
        description = "Removes Nylos that have been killed",
        tags = {"Socket, death, kill", "nylo"},
        enabledByDefault = false
)
@PluginDependency(SocketPlugin.class)
public class DeathIndicatorsPlugin extends Plugin
{
    @Inject
    private DeathIndicatorsConfig config;

    @Inject
    ConfigManager configManager;

    @Inject
    PluginManager pluginManager;

    @Inject
    private DeathIndicatorsOverlay overlay;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EventBus eventBus;

    @Inject
    private Hooks hooks;

    private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

    private ArrayList<NyloQ> nylos;

    private ArrayList<Method> reflectedMethods;
    private ArrayList<Plugin> reflectedPlugins;

    @Getter
    private ArrayList<NPC> deadNylos;

    @Getter
    private NyloQ maidenNPC;

    private ArrayList<Integer> hiddenIndices;

    private int partySize;
    private int ATTACK;
    private int STRENGTH;
    private int DEFENCE;
    private int RANGED;
    private int MAGIC;
    private boolean inNylo = false;

    @Provides
    DeathIndicatorsConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DeathIndicatorsConfig.class);
    }

    protected void startUp()
    {
        hooks.registerRenderableDrawListener(drawListener);
        ATTACK = -1;
        STRENGTH = -1;
        DEFENCE = -1;
        RANGED = -1;
        MAGIC = -1;
        deadNylos = new ArrayList<>();
        nylos = new ArrayList<>();
        hiddenIndices = new ArrayList<>();
        overlayManager.add(overlay);
        reflectedMethods = new ArrayList<>();
        reflectedPlugins = new ArrayList<>();

        for (Plugin p : pluginManager.getPlugins())
        {
            Method m;

            try
            {
                m = p.getClass().getDeclaredMethod("SocketDeathIntegration", Integer.TYPE);
            }
            catch (NoSuchMethodException var5)
            {
                continue;
            }

            reflectedMethods.add(m);
            reflectedPlugins.add(p);
        }

    }

    @Override
    protected void shutDown()
    {
        hooks.unregisterRenderableDrawListener(drawListener);
        deadNylos = null;
        nylos = null;
        hiddenIndices = null;
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        int smSmallHP = -1;
        int smBigHP = -1;
        int bigHP = -1;
        int smallHP = -1;
        int maidenHP = -1;
        if (partySize == 1)
        {
            bigHP = 16;
            smallHP = 8;
            maidenHP = 2625;
            smSmallHP = 2;
            smBigHP = 3;
        }
        else if (partySize == 2)
        {
            bigHP = 16;
            smallHP = 8;
            maidenHP = 2625;
            smSmallHP = 4;
            smBigHP = 6;
        }
        else if (partySize == 3)
        {
            bigHP = 16;
            smallHP = 8;
            maidenHP = 2625;
            smSmallHP = 6;
            smBigHP = 9;
        }
        else if (partySize == 4)
        {
            bigHP = 19;
            smallHP = 9;
            maidenHP = 3062;
            smSmallHP = 8;
            smBigHP = 12;
        }
        else if (partySize == 5)
        {
            bigHP = 22;
            smallHP = 11;
            maidenHP = 3500;
            smSmallHP = 10;
            smBigHP = 15;
        }

        int id = event.getNpc().getId();
        switch(id)
        {
            case 8342:
            case 10791://melee
            case 8343:
            case 10792://range
            case 8344:
            case 10793://mage
                nylos.add(new NyloQ(event.getNpc(), 0, smallHP));
                break;
            case 10775:
            case 10774:
            case 10776:
                nylos.add(new NyloQ(event.getNpc(), 0, smSmallHP));
                break;
            case 10777:
            case 10778:
            case 10779:
                nylos.add(new NyloQ(event.getNpc(), 0, smBigHP));
                break;
            case 8345:
            case 10794://melee
            case 8346:
            case 10795://range
            case 8347:
            case 10796://mage
            case 8351:
            case 10783:
            case 10800://melee aggro
            case 8352:
            case 10784:
            case 10801://range aggro
            case 8353:
            case 10785:
            case 10802://mage aggro
                nylos.add(new NyloQ(event.getNpc(), 0, bigHP));
                break;
            case 8360:
                NyloQ maidenTemp = new NyloQ(event.getNpc(), 0, maidenHP);
                nylos.add(maidenTemp);
                maidenNPC = maidenTemp;
        }

    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        if (nylos.size() != 0)
        {
            nylos.removeIf(q -> q.npc.equals(event.getNpc()));
        }

        if (deadNylos.size() != 0)
        {
            deadNylos.removeIf(q->q.equals(event.getNpc()));
        }

        int id = event.getNpc().getId();
        switch (id)
        {
            case NpcID.THE_MAIDEN_OF_SUGADINTI: //normal mode
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8361:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8362:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8363:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8364:
            case NpcID.THE_MAIDEN_OF_SUGADINTI_8365:
                maidenNPC = null;
                break;
        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired)
    {
        if (inNylo)
        {
            if (scriptPreFired.getScriptId() == 996)
            {
                final int[] intStack = client.getIntStack();
                final int intStackSize = client.getIntStackSize();
                final int widgetId = intStack[intStackSize - 4];
            }
        }
    }

    private boolean inRegion(int... regions)
    {
        if (client.getMapRegions() != null)
        {
            int[] mapRegions = client.getMapRegions();

            return Arrays.stream(mapRegions).anyMatch(i -> Arrays.stream(regions).anyMatch(j -> i == j));
        }

        return false;
    }

    private void postHit(int index, int dmg)
    {
        JSONArray data = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("index", index);
        message.put("damage", dmg);
        data.put(message);
        JSONObject send = new JSONObject();
        send.put("sDeath", data);
        eventBus.post(new SocketBroadcastPacket(send));
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
    {
        if (inNylo)
        {
            for(NyloQ q : nylos)
            {
                if (hitsplatApplied.getActor().equals(q.npc))
                {
                    if (hitsplatApplied.getHitsplat().getHitsplatType().equals(Hitsplat.HitsplatType.HEAL))
                    {
                        q.hp += hitsplatApplied.getHitsplat().getAmount();
                    }
                    else
                    {
                        q.hp -= hitsplatApplied.getHitsplat().getAmount();
                        q.queuedDamage -= hitsplatApplied.getHitsplat().getAmount();
                    }

                    if (q.hp <= 0)
                    {
                        deadNylos.removeIf((o) -> o.equals(q.npc));
                    }
                    else if (q.npc.getId() == 8360 || q.npc.getId() == 8361 || q.npc.getId() == 8362 || q.npc.getId() == 8363
                            || q.npc.getId() == 10822 || q.npc.getId() == 10823 || q.npc.getId() == 10824 || q.npc.getId() == 10825) {
                        double percent = (double) q.hp / (double) q.maxHP;
                        if (percent < 0.7D && q.phase == 0)
                        {
                            q.phase = 1;
                        }

                        if (percent < 0.5D && q.phase == 1)
                        {
                            q.phase = 2;
                        }

                        if (percent < 0.3D && q.phase == 2)
                        {
                            q.phase = 3;
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onSocketReceivePacket(SocketReceivePacket event)
    {
        if (inNylo)
        {
            try
            {
                JSONObject payload = event.getPayload();
                if (payload.has("sDeath"))
                {
                    JSONArray data = payload.getJSONArray("sDeath");
                    JSONObject jsonmsg = data.getJSONObject(0);
                    int index = jsonmsg.getInt("index");
                    int damage = jsonmsg.getInt("damage");

                    for(NyloQ q : nylos)
                    {
                        if (q.npc.getIndex() == index)
                        {
                            q.queuedDamage += damage;
                            if (q.npc.getId() == 8360 || q.npc.getId() == 8361 || q.npc.getId() == 8362 || q.npc.getId() == 8363
                                    || q.npc.getId() == 10822 || q.npc.getId() == 10823 || q.npc.getId() == 10824 || q.npc.getId() == 10825)
                            {
                                if (q.queuedDamage > 0)
                                {
                                    double percent = ((double) q.hp - (double) q.queuedDamage) / (double) q.maxHP;
                                    if (percent < 0.7D && q.phase == 0)
                                    {
                                        q.phase = 1;
                                    }

                                    if (percent < 0.5D && q.phase == 1)
                                    {
                                        q.phase = 2;
                                    }

                                    if (percent < 0.3D && q.phase == 2)
                                    {
                                        q.phase = 3;
                                    }
                                }
                            }
                            else if (q.hp - q.queuedDamage <= 0 && deadNylos.stream().noneMatch((o) -> o.getIndex() == q.npc.getIndex()))
                            {
                                deadNylos.add(q.npc);
                                if (config.hideNylo())
                                {
                                    setHiddenNpc(q.npc, true);
                                    q.hidden = true;
                                    if (reflectedPlugins.size() == reflectedMethods.size())
                                    {
                                        for (int i = 0; i < reflectedPlugins.size(); ++i)
                                        {
                                            try
                                            {
                                                Method tm = reflectedMethods.get(i);
                                                tm.setAccessible(true);
                                                tm.invoke(reflectedPlugins.get(i), q.npc.getIndex());
                                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ExceptionInInitializerError | NullPointerException var11)
                                            {
                                                log.debug("Failed on plugin: " + reflectedPlugins.get(i).getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setHiddenNpc(NPC npc, boolean hidden)
    {
        if (hidden)
        {
            hiddenIndices.add(npc.getIndex());
        }
        else
        {
            if (hiddenIndices.contains(npc.getIndex()))
            {
                hiddenIndices.remove((Integer) npc.getIndex());
            }
        }
    }

    void addToDamageQueue(int damage)
    {
        if (damage != -1)
        {
            Actor interacted = Objects.requireNonNull(client.getLocalPlayer()).getInteracting();
            if (interacted instanceof NPC)
            {
                NPC interactedNPC = (NPC) interacted;
                postHit(interactedNPC.getIndex(), damage);
            }

        }
    }

    @Subscribe
    public void onFakeXpDrop(FakeXpDrop event) throws InterruptedException
    {
        if (!inNylo)
        {
            return;
        }

        int xpdiff = event.getXp();
        String skill = event.getSkill().toString();
        if (!skill.equals("RANGED") && !skill.equals("MAGIC") && !skill.equals("STRENGTH") && !skill.equals("ATTACK") && !skill.equals("DEFENCE"))
        {
            return;
        }

        switch (skill)
        {
            case "MAGIC":
                xpdiff = event.getXp() - MAGIC;
                MAGIC = event.getXp();
                break;
            case "RANGED":
                xpdiff = event.getXp() - RANGED;
                RANGED = event.getXp();
                break;
            case "STRENGTH":
                xpdiff = event.getXp() - STRENGTH;
                STRENGTH = event.getXp();
                break;
            case "ATTACK":
                xpdiff = event.getXp() - ATTACK;
                ATTACK = event.getXp();
                break;
            case "DEFENCE":
                xpdiff = event.getXp() - DEFENCE;
                DEFENCE = event.getXp();
                break;
        }

        processXpDrop(String.valueOf(xpdiff), skill);
    }

    @Subscribe
    public void onStatChanged(StatChanged event) throws InterruptedException
    {
        if (!inNylo)
        {
            return;
        }

        int xpdiff = 0;
        String skill = event.getSkill().toString();
        if (!skill.equals("RANGED") && !skill.equals("MAGIC") && !skill.equals("STRENGTH") && !skill.equals("ATTACK") && !skill.equals("DEFENCE"))
        {
            return;
        }

        switch (skill)
        {
            case "MAGIC":
                xpdiff = event.getXp() - MAGIC;
                MAGIC = event.getXp();
                break;
            case "RANGED":
                xpdiff = event.getXp() - RANGED;
                RANGED = event.getXp();
                break;
            case "STRENGTH":
                xpdiff = event.getXp() - STRENGTH;
                STRENGTH = event.getXp();
                break;
            case "ATTACK":
                xpdiff = event.getXp() - ATTACK;
                ATTACK = event.getXp();
                break;
            case "DEFENCE":
                xpdiff = event.getXp() - DEFENCE;
                DEFENCE = event.getXp();
                break;
        }

        processXpDrop(String.valueOf(xpdiff), skill);
    }

    private void processXpDrop(String xpDrop, String skill) throws InterruptedException
    {
        int damage = 0;
        int weaponUsed = Objects.requireNonNull(Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition()).getEquipmentId(KitType.WEAPON);
        boolean poweredStave = (weaponUsed == 22323 || weaponUsed == 11905 || weaponUsed == 11907 || weaponUsed == 12899 || weaponUsed == 22292 || weaponUsed == 25731);
        if (client.getLocalPlayer().getAnimation() != 1979) //Barrage
        {
            switch (skill)
            {
                case "MAGIC":
                    // sang/tridents
                    if (poweredStave && client.getVar(VarPlayer.ATTACK_STYLE) != 3)
                    {
                        damage = (int) (Integer.parseInt(xpDrop) / 2.0);
                    }
                    break;
                case "ATTACK":
                case "STRENGTH":
                case "DEFENCE":
                    if (weaponUsed == 22325 || weaponUsed == 25739 || weaponUsed == 25736 || weaponUsed == 21015)
                        //Don't apply if weapon is scythe or dinhs
                    {
                        return;
                    }
                    if (poweredStave) //Powered Staves
                    {
                        if (client.getLocalPlayer().getAnimation() == 1979) //Barrage
                        {
                            return;
                        }
                        if (client.getVar(VarPlayer.ATTACK_STYLE) == 3)
                        {
                            damage = Integer.parseInt(xpDrop);
                        }
                    }
                    else
                    {
                        if (WeaponMap.StyleMap.get(weaponUsed).toString().equals("MELEE"))
                        {
                            damage = (int) (Integer.parseInt(xpDrop) / 4.0);
                        }
                    }

                    break;
                case "RANGED":
                    if (weaponUsed == 11959)
                    {
                        return;
                    }
                    if (client.getVar(VarPlayer.ATTACK_STYLE) == 3)
                    {
                        damage = (int) (Integer.parseInt(xpDrop) / 2.0);
                    }
                    else
                    {
                        damage = (int) (Integer.parseInt(xpDrop) / 4.0);
                    }
                    break;
            }

            addToDamageQueue(damage);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (client.getLocalPlayer() != null && MAGIC == -1)
        {
            initStatXp();
        }

        if (!inNylo)
        {
            if (inRegion(13122))
            {
                inNylo = true;
                partySize = 0;

                for (int i = 330; i < 335; i++)
                {
                    if (client.getVarcStrValue(i) != null && !client.getVarcStrValue(i).equals(""))
                    {
                        partySize++;
                    }
                }
            }
        }
        else if (!inRegion(13122))
        {
            inNylo = false;
            if (!hiddenIndices.isEmpty())
            {
                hiddenIndices.clear();
            }
            if (!nylos.isEmpty() || !deadNylos.isEmpty())
            {
                nylos.clear();
                deadNylos.clear();
            }
        }

        for (NyloQ q : nylos)
        {
            if (q.hidden)
            {
                q.hiddenTicks++;
                if (q.npc.getHealthRatio() != 0 && q.hiddenTicks > 5)
                {
                    q.hiddenTicks = 0;
                    q.hidden = false;
                    setHiddenNpc(q.npc, false);
                    deadNylos.removeIf((x) -> x.equals(q.npc));
                }
            }
        }

    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (config.deprioNylo())
        {
            client.setMenuEntries(updateMenuEntries(client.getMenuEntries()));
        }
    }

    private MenuEntry[] updateMenuEntries(MenuEntry[] menuEntries) {
        return Arrays.stream(menuEntries)
                .filter(filterMenuEntries).sorted((o1, o2) -> 0)
                .toArray(MenuEntry[]::new);
    }

    private final Predicate<MenuEntry> filterMenuEntries = entry -> {
        int id = entry.getIdentifier();
        String option = Text.standardize(entry.getOption(), true).toLowerCase();

        if (option.contains("attack") && deadNylos.contains(client.getCachedNPCs()[id]))
        {
            entry.setDeprioritized(true);
        }
        return true;
    };

    private void initStatXp()
    {
        ATTACK = client.getSkillExperience(Skill.ATTACK);
        STRENGTH = client.getSkillExperience(Skill.STRENGTH);
        DEFENCE = client.getSkillExperience(Skill.DEFENCE);
        RANGED = client.getSkillExperience(Skill.RANGED);
        MAGIC = client.getSkillExperience(Skill.MAGIC);
    }

    @VisibleForTesting
    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        if (renderable instanceof NPC)
        {
            NPC npc = (NPC) renderable;
            return !hiddenIndices.contains(npc.getIndex());
        }

        return true;
    }
}