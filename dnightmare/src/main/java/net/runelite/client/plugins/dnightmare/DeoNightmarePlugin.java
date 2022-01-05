package net.runelite.client.plugins.dnightmare;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Arrays;

@Extension
@PluginDescriptor(name = "[D] Nightmare", description = "Nightmare time tracking")
public class DeoNightmarePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private InfoBoxManager infoBoxManager;

    private static final int NM_ROOM_MASK = 66994112;

    private static final int NM_ROOM = 58205888;

    private static final int NIGHTMARE_P1 = 9425;

    private static final int NIGHTMARE_P2 = 9426;

    private static final int NIGHTMARE_P3 = 9427;

    private static final int NIGHTMARE_P1_PILLARS = 9428;

    private static final int NIGHTMARE_P2_PILLARS = 9429;

    private static final int NIGHTMARE_P3_PILLARS = 9430;

    private static final int NIGHTMARE_SLEEPWALKERS = 9431;

    private static final int NIGHTMARE_DOWN = 9432;

    private static final int NIGHTMARE_DEATH = 9433;

    private static final int PHOSANI_P1 = 9416;

    private static final int PHOSANI_P2 = 9417;

    private static final int PHOSANI_P3 = 9418;

    private static final int PHOSANI_P4 = 11153;

    private static final int PHOSANI_P5 = 11154;

    private static final int PHOSANI_P1_PILLARS = 9419;

    private static final int PHOSANI_P2_PILLARS = 9420;

    private static final int PHOSANI_P3_PILLARS = 9421;

    private static final int PHOSANI_P4_PILLARS = 11155;

    private static final int PHOSANI_SLEEPWALKERS = 9422;

    private static final int PHOSANI_DOWN = 9423;

    private static final int PHOSANI_DEATH = 9424;

    private NPC nm;

    private boolean dirty;

    private int phase;

    private NightmareInfoBox nib;

    int fight_timer = -1;

    int phase_timer = -1;

    int subph_timer = -1;

    int[] phase_splits = new int[6];

    protected void shutDown() throws Exception {
        fight_timer = phase_timer = subph_timer = -1;
        nm = null;
        Arrays.fill(phase_splits, -1);
        if (nib != null) {
            infoBoxManager.removeInfoBox(nib);
            nib = null;
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned e) {
        if (!is_in_noa())
            return;
        NPC npc = e.getNpc();
        if (get_noa_npc_type(npc) == -1)
            return;
        nm = npc;
        System.out.println("Found NOA: " + nm.getIndex());
        fight_timer = phase_timer = subph_timer = client.getTickCount();
        Arrays.fill(phase_splits, -1);
        dirty = true;
        phase = -1;
    }

    @Subscribe
    public void onNpcChanged(NpcChanged e) {
        if (e.getNpc() != nm)
            return;
        onNightmareChanged(e.getOld().getId());
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned e) throws Exception {
        if (e.getNpc() != nm)
            return;
        shutDown();
    }

    @Subscribe
    public void onChatMessage(ChatMessage e) {
        if (e.getMessage().contains("All four totems are fully charged.")) {
            int tick_count = client.getTickCount() + 4;
            mes(tick_count, "pillars");
            subph_timer = tick_count;
            phase_splits[phase] = tick_count - phase_timer;
            if (get_noa_npc_type(nm) == 0 && phase == 3)
                phase_splits[0] = tick_count - fight_timer;
        }
    }

    private void onNightmareChanged(int oldid) {
        System.out.println("Nm change from " + oldid + " to " + nm.getId());
        int tick_count = client.getTickCount();
        switch (nm.getId()) {
            case PHOSANI_P1:
            case PHOSANI_P2:
            case PHOSANI_P3:
            case NIGHTMARE_P1:
            case NIGHTMARE_P2:
            case NIGHTMARE_P3:
            case PHOSANI_P4:
            case PHOSANI_P5:
                if (oldid == NIGHTMARE_DOWN || oldid == PHOSANI_DOWN) {
                    if (nib == null) {
                        nib = new NightmareInfoBox(client, this);
                        infoBoxManager.addInfoBox(nib);
                    }
                    Arrays.fill(phase_splits, -1);
                    phase = 1;
                    fight_timer = tick_count;
                    dirty = false;
                } else {
                    mes(tick_count, "sleepwalkers");
                    phase++;
                }
                phase_timer = tick_count;
                subph_timer = tick_count;
                break;
            case PHOSANI_DEATH:
                phase_splits[5] = tick_count - phase_timer;
                phase_splits[0] = tick_count - fight_timer;
            case PHOSANI_P1_PILLARS:
            case PHOSANI_P2_PILLARS:
            case PHOSANI_P3_PILLARS:
            case NIGHTMARE_P1_PILLARS:
            case NIGHTMARE_P2_PILLARS:
            case NIGHTMARE_P3_PILLARS:
            case PHOSANI_P4_PILLARS:
                mes(tick_count, "boss");
                subph_timer = tick_count;
                break;
        }
    }

    private void mes(int tick_count, String type) {
        StringBuilder sb = new StringBuilder();
        if (get_noa_npc_type(nm) == 1)
            sb.append("Phosani's ");
        sb.append("Nightmare P");
        if (phase == -1) {
            sb.append('?');
        } else {
            sb.append(phase);
        }
        sb.append(' ').append(type).append(' ');
        sb.append("complete! Duration: <col=ff0000>");
        sb.append(to_mmss(tick_count - subph_timer));
        sb.append("</col>");
        if (!dirty) {
            sb.append(" Total: <col=ff0000>");
            sb.append(to_mmss(tick_count - fight_timer));
            sb.append("</col>");
        }
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", sb.toString(), null);
    }

    private int get_noa_npc_type(NPC npc) {
        if (npc == null)
            return -1;
        switch (npc.getId()) {
            case NIGHTMARE_P1:
            case NIGHTMARE_P2:
            case NIGHTMARE_P3:
            case NIGHTMARE_P1_PILLARS:
            case NIGHTMARE_P2_PILLARS:
            case NIGHTMARE_P3_PILLARS:
            case NIGHTMARE_SLEEPWALKERS:
            case NIGHTMARE_DOWN:
            case NIGHTMARE_DEATH:
                return 0;
            case PHOSANI_P1:
            case PHOSANI_P2:
            case PHOSANI_P3:
            case PHOSANI_P1_PILLARS:
            case PHOSANI_P2_PILLARS:
            case PHOSANI_P3_PILLARS:
            case PHOSANI_SLEEPWALKERS:
            case PHOSANI_DOWN:
            case PHOSANI_DEATH:
            case PHOSANI_P4:
            case PHOSANI_P5:
            case PHOSANI_P4_PILLARS:
                return 1;
        }
        return -1;
    }

    String to_mmss(int ticks) {
        return (client.getVarbitValue(11866) == 1) ? MiscUtil.to_mmss_precise(ticks) :
                MiscUtil.to_mmss(ticks);
    }

    private boolean is_in_noa() {
        WorldPoint wp = client.getLocalPlayer().getWorldLocation();
        int x = wp.getX() - client.getBaseX();
        int y = wp.getY() - client.getBaseY();
        int template = client.getInstanceTemplateChunks()[client.getPlane()][x /
                8][y / 8];
        return ((template & 0x3FE3FC0) == NM_ROOM);
    }

    boolean is_timer_dirty() {
        return dirty;
    }
}
