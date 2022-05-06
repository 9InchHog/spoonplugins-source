package net.runelite.client.plugins.socket.plugins.deathindicators;

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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Hitsplat.HitsplatType;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
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
    private DeathIndicatorsOverlay overlay;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    private ArrayList<NyloQ> nylos;
    @Inject
    private EventBus eventBus;
    private boolean inNylo = false;

    @Getter
    private ArrayList<NPC> deadNylos;
    @Getter
    private NyloQ maidenNPC;

    private ArrayList<Integer> hiddenIndices;
    private int partySize;

    @Inject
    PluginManager pluginManager;
    private ArrayList<Method> reflectedMethods;
    private ArrayList<Plugin> reflectedPlugins;

    @Provides
    DeathIndicatorsConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DeathIndicatorsConfig.class);
    }

    @Override
    protected void startUp()
    {
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
        deadNylos = null;
        nylos = null;
        hiddenIndices = null;
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        if (partySize != -1)
        {
            int bigHP = -1;
            int smallHP = -1;
            int maidenHP = -1;
            if (partySize < 4)
            {
                bigHP = 16;
                smallHP = 8;
                maidenHP = 2625;
            }
            else if (partySize == 4)
            {
                bigHP = 19;
                smallHP = 9;
                maidenHP = 3062;
            }
            else if (partySize == 5)
            {
                bigHP = 22;
                smallHP = 11;
                maidenHP = 3500;
            }

            int id = event.getNpc().getId();
            switch (id)
            {
                case 8342:
                case 8343:
                case 8344:
                case 10791:
                case 10792:
                case 10793:
                case 10797:
                case 10798:
                case 10799:
                    nylos.add(new NyloQ(event.getNpc(), 0, smallHP));
                    break;
                case 8345:
                case 8346:
                case 8347:
                case 8351:
                case 8352:
                case 8353:
                case 10794:
                case 10795:
                case 10796:
                case 10800:
                case 10801:
                case 10802:
                    nylos.add(new NyloQ(event.getNpc(), 0, bigHP));
                    break;
                case NpcID.THE_MAIDEN_OF_SUGADINTI:
                case NpcID.THE_MAIDEN_OF_SUGADINTI_10822:
                    NyloQ maidenTemp = new NyloQ(event.getNpc(), 0, maidenHP);
                    nylos.add(maidenTemp);
                    maidenNPC = maidenTemp;
                    break;
            }

        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        if (nylos.size() != 0)
        {
            nylos.removeIf((q) -> q.npc.equals(event.getNpc()));
        }

        if (deadNylos.size() != 0)
        {
            deadNylos.removeIf((q) -> q.equals(event.getNpc()));
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
                int[] intStack = client.getIntStack();
                int intStackSize = client.getIntStackSize();
                int widgetId = intStack[intStackSize - 4];

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
            Iterator<NyloQ> nyloQIterator = nylos.iterator();

            while (true)
            {
                NyloQ q;
                do
                {
                    if (!nyloQIterator.hasNext())
                    {
                        return;
                    }

                    q = nyloQIterator.next();
                } while (!hitsplatApplied.getActor().equals(q.npc));

                if (hitsplatApplied.getHitsplat().getHitsplatType().equals(HitsplatType.HEAL))
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
                    NyloQ finalQ = q;
                    deadNylos.removeIf((o) -> o.equals(finalQ.npc));
                }
                else if (q.npc.getId() == 8360 || q.npc.getId() == 8361 || q.npc.getId() == 8362 || q.npc.getId() == 8363
                        || q.npc.getId() == 10822 || q.npc.getId() == 10823 || q.npc.getId() == 10824 || q.npc.getId() == 10825)
                {
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
                    Iterator<NyloQ> nyloQIterator = nylos.iterator();

                    while (true)
                    {
                        NyloQ q;
                        do
                        {
                            if (!nyloQIterator.hasNext())
                            {
                                return;
                            }

                            q = nyloQIterator.next();
                        } while (q.npc.getIndex() != index);

                        q.queuedDamage += damage;
                        NyloQ finalQ = q;
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
                        else if (q.hp - q.queuedDamage <= 0 && deadNylos.stream().noneMatch((o) -> o.getIndex() == finalQ.npc.getIndex()))
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
                                        }
                                        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ExceptionInInitializerError | NullPointerException var11)
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
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setHiddenNpc(NPC npc, boolean hidden)
    {

        List<Integer> newHiddenNpcIndicesList = client.getHiddenNpcIndices();
        if (hidden)
        {
            newHiddenNpcIndicesList.add(npc.getIndex());
            hiddenIndices.add(npc.getIndex());
        }
        else
        {
            if (newHiddenNpcIndicesList.contains(npc.getIndex()))
            {
                newHiddenNpcIndicesList.remove((Integer) npc.getIndex());
            }
        }
        client.setHiddenNpcIndices(newHiddenNpcIndicesList);

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

    private void processXpDrop(int widgetId) throws InterruptedException
    {
        Widget xpDrop = client.getWidget(WidgetInfo.TO_GROUP(widgetId), WidgetInfo.TO_CHILD(widgetId));
        if (xpDrop != null)
        {
            Widget[] children = xpDrop.getChildren();
            Widget text = children[0];
            String cleansedXpDrop = cleanseXpDrop(text.getText());
            int damage = -1;
            int weaponUsed = Objects.requireNonNull(Objects.requireNonNull(client.getLocalPlayer()).getPlayerComposition()).getEquipmentId(KitType.WEAPON);
            if (client.getLocalPlayer().getAnimation() != 1979)
            {
                // magic sprite
                if (Arrays.stream(children).skip(1L).filter(Objects::nonNull).mapToInt(Widget::getSpriteId).anyMatch((id) -> id == 202))
                {
                    // sang/tridents
                    if (weaponUsed == 22323 || weaponUsed == 11905 || weaponUsed == 11907 || weaponUsed == 12899 || weaponUsed == 22292 || weaponUsed == 25731)
                    {
                        if (client.getLocalPlayer().getAnimation() == 1979)
                        {
                            return;
                        }

                        if (client.getVarbitValue(4696) == 0)
                        {
                            if (client.getVar(VarPlayer.ATTACK_STYLE) != 3)
                            {
                                damage = (int) ((double) Integer.parseInt(cleansedXpDrop) / 2.0D);
                            }
                        }
                        else
                        {
                            if (client.getVar(VarPlayer.ATTACK_STYLE) == 3)
                            {
                                damage = (int) Math.round((double) Integer.parseInt(cleansedXpDrop) / 3.6667D);
                            }
                            else
                            {
                                damage = (int) Math.round((double) Integer.parseInt(cleansedXpDrop) / 3.3334D);
                            }
                        }
                    }
                }
                // att, str, def sprite
                else if (Arrays.stream(children).skip(1L).filter(Objects::nonNull).mapToInt(Widget::getSpriteId).anyMatch((id) -> id == 197 || id == 198 || id == 199))
                {
                    if (weaponUsed == 22325 || weaponUsed == 25739 || weaponUsed == 25736 || weaponUsed == 21015) //Don't apply if weapon is scythe
                    {
                        return;
                    }

                    if (client.getVarbitValue(4696) == 0)
                    {
                        // checking if casting on long range
                        if (weaponUsed != 22323 && weaponUsed != 11905 && weaponUsed != 11907 && weaponUsed != 12899 && weaponUsed != 22292 && weaponUsed != 25731) //Powered Staves
                        {
                            if (weaponUsed == 12006)
                            {
                                if (client.getVar(VarPlayer.ATTACK_STYLE) == 1)
                                {
                                    if (Arrays.stream(children).skip(1L).filter(Objects::nonNull).mapToInt(Widget::getSpriteId).anyMatch((id) -> id == 197))
                                    {
                                        damage = (int) Math.round(3.0D * (double) Integer.parseInt(cleansedXpDrop) / 4.0D);
                                    }
                                }
                                else
                                {
                                    damage = Integer.parseInt(cleansedXpDrop) / 4;
                                }
                            }
                            else
                            {
                                damage = Integer.parseInt(cleansedXpDrop) / 4;
                            }
                        }
                        else
                        {
                            // :gottago: if barrage
                            if (client.getLocalPlayer().getAnimation() == 1979)
                            {
                                return;
                            }

                            if (client.getVarbitValue(4696) == 0 && client.getVar(VarPlayer.ATTACK_STYLE) == 3)
                            {
                                damage = Integer.parseInt(cleansedXpDrop);
                            }
                        }
                    }
                    else
                    {
                        damage = (int) Math.round((double) Integer.parseInt(cleansedXpDrop) / 5.3333D);
                    }
                }
                // range sprite
                else if (Arrays.stream(children).skip(1L).filter(Objects::nonNull).mapToInt(Widget::getSpriteId).anyMatch((id) -> id == 200))
                {
                    // :gottago: if chins
                    if (weaponUsed == 11959)
                    {
                        return;
                    }

                    if (client.getVarbitValue(4696) == 0)
                    {
                        damage = (int) ((double) Integer.parseInt(cleansedXpDrop) / 4.0D);
                    }
                    else
                    {
                        damage = (int) Math.round((double) Integer.parseInt(cleansedXpDrop) / 5.333D);
                    }
                }

                addToDamageQueue(damage);
            }
        }
    }

    /**
     * should cleanse the XP drop to remove the damage number in parens if the player uses that pluin
     * @param text the xp drop widget text
     * @return the base xp drop
     */
    private String cleanseXpDrop(String text)
    {
        if (text.contains("<"))
        {
            if (text.contains("<img=11>"))
            {
                text = text.substring(9);
            }
            if (text.contains("<"))
            {
                text = text.substring(0, text.indexOf("<"));
            }
        }
        return text;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (inRegion(13122, 12613))
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
        else
        {
            inNylo = false;
            if (!hiddenIndices.isEmpty())
            {
                List<Integer> newHiddenNpcIndicesList = client.getHiddenNpcIndices();
                newHiddenNpcIndicesList.removeAll(hiddenIndices);
                client.setHiddenNpcIndices(newHiddenNpcIndicesList);
                hiddenIndices.clear();
            }
            if (!nylos.isEmpty() || !deadNylos.isEmpty())
            {
                nylos.clear();
                deadNylos.clear();
            }
        }
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (config.deprioNylo()) {
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

        if (option.contains("attack") && deadNylos.contains(client.getCachedNPCs()[id])) {
            entry.setDeprioritized(true);
        }
        return true;
    };
}