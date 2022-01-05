package net.runelite.client.plugins.objecthider;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Extension
@PluginDescriptor(
        name = "Ground Object Hider",
        description = "Hides Ground Objects. A selector is used to choose objects to hide.",
        tags = {"external", "objects", "memory", "usage", "ground", "decorations", "performance"}
)
public class ObjectHiderPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ObjectHiderConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private ObjectHiderOverlay overlay;

    @Inject
    private KeyManager keyManager;

    @Inject
    private MouseManager mouseManager;

    public boolean selectGroundObjectMode = false;

    private HashMap<WorldPoint, GroundObject> hiddenObjects = new HashMap<>();

    /**
     * groundObjectsKeyListener is an instance of `HotkeyListener` designed to
     * let the user pick a tile-ingame to have it's Ground Object hidden.
     */
    private final HotkeyListener groundObjectsKeyListener = new HotkeyListener(() -> config.hideGroundObjectKey()) {
        @Override
        public void keyPressed(KeyEvent e) {
            if (config.hideGroundObjectKey().matches(e)) {
                selectGroundObjectMode = true;
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if (config.hideGroundObjectKey().matches(e)) {
                selectGroundObjectMode = false;
            }
        }
    };

    /**
     * mouseListener is an instance of `MouseListener` designed solely to let
     * the user pick a tile in-game to have it's Ground Object hidden.
     */
    private final MouseListener mouseListener = new MouseListener() {
        @Override
        public MouseEvent mouseClicked(MouseEvent mouseEvent) {
            if (SwingUtilities.isRightMouseButton(mouseEvent)) {
                if (!selectGroundObjectMode) {
                    return mouseEvent;
                }
                final Tile tile = client.getSelectedSceneTile();
                if (tile == null) {
                    return mouseEvent;
                }
                // have a selected tile, in a suitable mode, so consume event:
                mouseEvent.consume();

                // get current list:
                final List<Integer> curGroundHide =  new ArrayList<>(getGroundObjects());
                final GroundObject obj = tile.getGroundObject();
                if (obj != null) {
                    if (!curGroundHide.contains(obj.getId())) {
                        curGroundHide.add(obj.getId());
                        log.debug("added Ground Object with ID: {}", obj.getId());
                    }
                    config.setGroundObjectsToHide(Text.toCSV(curGroundHide.stream().map(String::valueOf).collect(Collectors.toList())));
                }
            }
            return mouseEvent;
        }

        @Override
        public MouseEvent mousePressed(MouseEvent mouseEvent) {
            if (selectGroundObjectMode && SwingUtilities.isRightMouseButton(mouseEvent)) {
                mouseEvent.consume();
            }
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseReleased(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseEntered(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseExited(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseDragged(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseMoved(MouseEvent mouseEvent) {
            return mouseEvent;
        }
    };

    @Provides
    ObjectHiderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ObjectHiderConfig.class);
    }


    @Override
    protected void startUp() {
        clientThread.invoke(this::rebuildObjects);
        keyManager.registerKeyListener(groundObjectsKeyListener);
        mouseManager.registerMouseListener(mouseListener);
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        // on shutDown make sure to remove the draw callbacks and clear lists:
        keyManager.unregisterKeyListener(groundObjectsKeyListener);
        mouseManager.unregisterMouseListener(mouseListener);
        overlayManager.remove(overlay);
        unhideAllGroundObjects();
        this.hiddenObjects = new HashMap<>();  // free any dangling references
    }

    /**
     * unhideAllGroundObjects iterates through the scene and restores any
     * Ground Objects that have been hidden by the plugin.
     */
    private void unhideAllGroundObjects() {
        final Scene scene = client.getScene();
        final Tile[][][] tiles = scene.getTiles();
        // for each tile:
        for (int plane = 0; plane < tiles.length; plane++) {
            for (int x = 0; x < tiles[plane].length; x++) {
                for (int y = 0; y < tiles[plane][x].length; y++) {
                    // if it is null, go to next one:
                    if (tiles[plane][x][y] == null) {
                        continue;
                    }
                    final WorldPoint location = tiles[plane][x][y].getWorldLocation();
                    // if we have hidden a Ground Object on that tile, restore it:
                    if (this.hiddenObjects.containsKey(location)) {
                        tiles[plane][x][y].setGroundObject(this.hiddenObjects.get(location));
                        // and make sure to sync the list:
                        this.hiddenObjects.remove(location);
                    }
                }
            }
        }
    }

    /**
     * hideGroundObjectOnTile removes the Ground Object `obj`, if it is present
     * , from tile `tile`.
     * It is stored in memory before removing, so that it can be restored if
     * need be later on.
     *
     * Note that, as a safeguard, Ground Objects that are intractable will not
     * be hidden. The return value will be false in that case.
     *
     * Certain disallowed objects are also prevented.
     *
     * @param tile - the Tile to remove from
     * @param obj - the Ground Object to remove
     * @return whether the operation was successful
     */
    private boolean hideGroundObjectOnTile(Tile tile, GroundObject obj) {
        // if either are null, don't do anything
        if (tile == null || obj == null) {
            return false;
        }
        final int[] disallowedObjIds = {41750, 41751, 41752, 41753};  // TOB
        int objId = obj.getId();
        if (Arrays.stream(disallowedObjIds).anyMatch(v -> v == objId)) {
            log.debug("hiding of a disallowed Ground Object was prevented ({})", objId);
            return false;
        }

        final ObjectComposition oc = client.getObjectDefinition(objId);
        if (oc != null) {
            if (Arrays.stream(oc.getActions()).anyMatch(a -> a != null && !a.equals("Examine"))) {
                log.debug("hiding of an intractable Ground Object was prevented ({})", objId);
                return false;
            }
        }
        hiddenObjects.put(tile.getWorldLocation(), obj);
        tile.setGroundObject(null);
        return true;
    }

    /**
     * rebuildObjects iterates through the scene to look for Ground Objects to
     * hide. Further, if any Ground Objects were previously hidden that should
     * not be, they are restored. Typically called on a config change.
     */
    private void rebuildObjects() {
        final Scene scene = client.getScene();
        final Tile[][][] tiles = scene.getTiles();

        for (int plane = 0; plane < tiles.length; plane++) {
            for (int x = 0; x < tiles[plane].length; x++) {
                for (int y = 0; y < tiles[plane][x].length; y++) {
                    final Tile currentTile = tiles[plane][x][y];
                    if (currentTile == null) {
                        continue;
                    }
                    // look for a matching Ground Object on that tile:
                    final GroundObject groundObj = currentTile.getGroundObject();
                    if (groundObj == null) {
                        // have we hidden something that shouldn't be hidden any more?
                        // look through `this.hiddenObjects` for this tile, and potentially restore:
                        if (this.hiddenObjects.containsKey(currentTile.getWorldLocation())) {
                            GroundObject oHidden = this.hiddenObjects.get(currentTile.getWorldLocation());
                            if (!getGroundObjects().contains(oHidden.getId())) {
                                currentTile.setGroundObject(oHidden);
                                this.hiddenObjects.remove(currentTile.getWorldLocation());
                            }
                        }
                        continue;
                    }
                    if (config.getHideAll()) {
                        hideGroundObjectOnTile(currentTile, currentTile.getGroundObject());
                    } else {
                        // tile has a ground object, so maybe add it to the hide list
                        for (Integer hideObjID : getGroundObjects()) {
                            if (groundObj.getId() == hideObjID) {
                                hideGroundObjectOnTile(currentTile, groundObj);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * getGroundObjects retrieves the list of Ground Objects to hide from the
     * config, and transforms into a `List<Integer>` for consumption.
     *
     * If something goes wrong, an empty list will be returned.
     * @return configured list of Ground Objects to hide
     */
    List<Integer> getGroundObjects() {
        try {
            return intsFromCSVString(config.getGroundObjectsToHide());
        } catch (NumberFormatException ex) {
            log.warn("unable to load Ground Objects to hide: bad input: {}", ex.toString());
            return Collections.emptyList();
        }
    }

    /**
     * intsFromCSVStrong takes a String containing a list of Integers and
     * returns those Integers in a `List<Integer>` format.
     * @param val - the string containing integers to parse
     * @return a list of integers
     * @throws NumberFormatException - if the string contains non-integers or
     * is badly formatted.
     */
    private static List<Integer> intsFromCSVString(String val) throws NumberFormatException {
        // parse a string of CSV integers:
        if (val.isEmpty()) {
            return Collections.emptyList();
        }
        return Text.fromCSV(val).stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    /**
     * onGroundObjectSpawned listens for newly-spawned Ground Objects in case
     * they should be hidden.
     * @param event - the spawn event
     */
    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        final GroundObject obj = event.getGroundObject();
        if (obj == null || event.getTile() == null) {
            return;
        }
        if (config.getHideAll() || getGroundObjects().contains(obj.getId())) {
            hideGroundObjectOnTile(event.getTile(), obj);
        }
    }

    /**
     * onGameTick listens for game ticks to schedule a regular garbage
     * collection of Game Objects that are no longer in the scene.
     *
     * @param event - the tick event
     */
    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getTickCount() % 100 == 0) {  // every 60 seconds roughly
            // to avoid the list of hidden objects growing boundlessly as a player moves around, we perform
            // a regular garbage collection. GC if:
            // - world location of hidden object is no longer in scene
            final List<WorldPoint> toRemove = new ArrayList<>();
            for (WorldPoint wp : this.hiddenObjects.keySet()) {
                if (!wp.isInScene(client)) {
                    toRemove.add(wp);
                }
            }
            for (WorldPoint wp : toRemove) {
                this.hiddenObjects.remove(wp);
            }
            log.debug("GCed {} tiles.", toRemove.size());
        }
    }
    /**
     * onGroundObjectDepawned listens for newly-despawned Ground Objects in
     * case they should be removed from the in-memory list.
     * @param event - the despawn event
     */
    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        final Tile t = event.getTile();
        if (t == null) {
            return;
        }
        final WorldPoint loc = t.getWorldLocation();
        if (loc != null) {
            this.hiddenObjects.remove(loc);
        }
    }

    /**
     * onConfigChanged listens for changes to the plugin configuration to
     * ensure the client is synchronised when the config changes.
     *
     * @param configChanged - the change event (not used)
     */
    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (!configChanged.getGroup().equals("objecthider")) {
            return;
        }
        clientThread.invoke(this::rebuildObjects);
    }
}
