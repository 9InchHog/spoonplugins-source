package net.runelite.client.plugins.cclumphelper;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Extension
@PluginDescriptor(
        name = "cMaidenClumpHelper",
        description = "Hides crabs and shows %s",
        tags = {"maiden", "tob", "clump", "helper"},
        enabledByDefault = false
)
public class cClumpHelperPlugin extends Plugin implements KeyListener {
    private static final Logger log = LoggerFactory.getLogger(cClumpHelperPlugin.class);

    @Inject
    private cClumpHelperOverlay corpOverlay;

    @Inject
    private Client client;

    @Inject
    private cClumpHelperConfig config;

    @Inject
    private KeyManager keyManager;

    @Provides
    cClumpHelperConfig getConfig(ConfigManager configManager) {
        return (cClumpHelperConfig)configManager.getConfig(cClumpHelperConfig.class);
    }

    @Getter
    private boolean maidenActive = false;

    private NPC maidenNPC;

    private boolean hiddenState;

    private final int MAIDEN_FIRST = 8360;

    private final int MAIDEN_DEAD = 8365;

    private final int MAIDEN_SECOND = 8361;

    private final int MAIDEN_THIRD = 8362;

    private final int MAIDEN_LAST = 8363;

    private int spawn50s;

    private ArrayList<MaidenCrab> crabs;

    private boolean scuffedFlag = false;

    private int scuffTick = 0;

    private ArrayList<Integer> hiddenIndices;

    protected void startUp() throws Exception {
        client.setIsHidingEntities(true);
        hiddenIndices = new ArrayList<>();
        hiddenState = false;
        crabs = new ArrayList<>();
        keyManager.registerKeyListener(this);
        spawn50s = -1;
    }

    protected void shutDown() {
        client.setIsHidingEntities(false);
        clearHiddenNpcs();
        hiddenIndices = null;
    }

    private void addCrab(String name, boolean scuffed, NPC npc) {
        if (!scuffedFlag)
            if (scuffed) {
                scuffedFlag = true;
                scuffTick = client.getTickCount();
            }
        crabs.add(new MaidenCrab(name, scuffed, npc));
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (config.useTime() && spawn50s != 0)
            if (config.tickAmount() + spawn50s == client.getTickCount()) {
                hiddenState = true;
                updateHidden();
                spawn50s = 0;
            }
    }

    private void maidenNylosSpawned(NPC npc) {
        int x = npc.getWorldLocation().getRegionX();
        int y = npc.getWorldLocation().getRegionY();
        String proc = "";
        if (maidenNPC.getId() == 8361 || maidenNPC.getId() == 10815 || maidenNPC.getId() == 10823) {
            proc = " 70";
            System.out.println("70s spawned");
        }
        if (maidenNPC.getId() == 8362 || maidenNPC.getId() == 10816 || maidenNPC.getId() == 10824) {
            proc = " 50";
            System.out.println("50s spawned");
        }
        if (maidenNPC.getId() == 8363 || maidenNPC.getId() == 10817 || maidenNPC.getId() == 10825) {
            proc = " 30";
            System.out.println("30s spawned");
        }
        if (proc.contains("50"))
            spawn50s = client.getTickCount();
        if (x == 21 && y == 40)
            addCrab("N1" + proc, false, npc);
        if (x == 22 && y == 41)
            addCrab("N1" + proc, true, npc);
        if (x == 25 && y == 40)
            addCrab("N2" + proc, false, npc);
        if (x == 26 && y == 41)
            addCrab("N2" + proc, true, npc);
        if (x == 29 && y == 40)
            addCrab("N3" + proc, false, npc);
        if (x == 30 && y == 41)
            addCrab("N3" + proc, true, npc);
        if (x == 33 && y == 40)
            addCrab("N4 (1)" + proc, false, npc);
        if (x == 34 && y == 41)
            addCrab("N4 (1)" + proc, true, npc);
        if (x == 33 && y == 38)
            addCrab("N4 (2)" + proc, false, npc);
        if (x == 34 && y == 39)
            addCrab("N4 (2)" + proc, true, npc);
        if (x == 21 && y == 20)
            addCrab("S1" + proc, false, npc);
        if (x == 22 && y == 19)
            addCrab("S1" + proc, true, npc);
        if (x == 25 && y == 20)
            addCrab("S2" + proc, false, npc);
        if (x == 26 && y == 19)
            addCrab("S2" + proc, true, npc);
        if (x == 29 && y == 20)
            addCrab("S3" + proc, false, npc);
        if (x == 30 && y == 19)
            addCrab("S3" + proc, true, npc);
        if (x == 33 && y == 20)
            addCrab("S4 (1)" + proc, false, npc);
        if (x == 34 && y == 19)
            addCrab("S4 (1)" + proc, true, npc);
        if (x == 33 && y == 22)
            addCrab("S4 (2)" + proc, false, npc);
        if (x == 34 && y == 20)
            addCrab("S4 (2)" + proc, true, npc);
    }

    private void maidenNylosDespawned(NPC npc) {
        if (crabs.size() != 0) {
            ArrayList<MaidenCrab> toRemove = new ArrayList<>();
            for (MaidenCrab c : crabs) {
                if (c.getIndex() == npc.getIndex())
                    toRemove.add(c);
            }
            for (MaidenCrab c : toRemove) {
                crabs.remove(c);
                setHiddenNpc(c.npc, false);
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        switch (event.getNpc().getId()) {
            case 8360: //normal mode
            case 8361:
            case 8362:
            case 8363:
            case 10814: //story mode
            case 10815:
            case 10816:
            case 10817:
            case 10822: //hard mode
            case 10823:
            case 10824:
            case 10825:
                maidenNPC = null;
                maidenActive = false;
                hiddenState = false;
                spawn50s = 0;
                break;
            case 8366:
            case 10828:
                if (maidenActive)
                    maidenNylosDespawned(event.getNpc());
                break;
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        switch (event.getNpc().getId()) {
            case 8360: //normal mode
            case 8361:
            case 8362:
            case 8363:
            case 10814: //story mode
            case 10815:
            case 10816:
            case 10817:
            case 10822: //hard mode
            case 10823:
            case 10824:
            case 10825:
                maidenNPC = event.getNpc();
                hiddenState = false;
                spawn50s = 0;
                maidenActive = true;
                break;
            case 8366:
            case 10820:
            case 10828:
                if (maidenActive)
                    maidenNylosSpawned(event.getNpc());
                break;
        }
    }

    private void updateHidden() {
        for (MaidenCrab m : crabs) {
            if (m.getName().contains("70"))
                setHiddenNpc(m.npc, hiddenState);
        }
    }

    private void setHiddenNpc(NPC npc, boolean hidden) {
        List<Integer> newHiddenNpcIndicesList = client.getHiddenNpcIndices();
        if (hidden) {
            newHiddenNpcIndicesList.add(npc.getIndex());
            hiddenIndices.add(npc.getIndex());
        } else {
            if (newHiddenNpcIndicesList.contains(npc.getIndex())) {
                newHiddenNpcIndicesList.remove((Integer) npc.getIndex());
                hiddenIndices.remove((Integer) npc.getIndex());
            }
        }
        client.setHiddenNpcIndices(newHiddenNpcIndicesList);
    }

    private void clearHiddenNpcs() {
        if (!hiddenIndices.isEmpty()) {
            List<Integer> newHiddenNpcIndicesList = client.getHiddenNpcIndices();
            newHiddenNpcIndicesList.removeAll(hiddenIndices);
            client.setHiddenNpcIndices(newHiddenNpcIndicesList);
            hiddenIndices.clear();
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        if (config.useKeyBind() && config.hotkey().matches(e)) {
            if (maidenActive) {
                hiddenState = !hiddenState;
                updateHidden();
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
}
