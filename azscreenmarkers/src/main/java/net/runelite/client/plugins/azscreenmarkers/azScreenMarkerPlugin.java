package net.runelite.client.plugins.azscreenmarkers;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.azscreenmarkers.ui.ScreenMarkerPluginPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Extension
@PluginDescriptor(
        name = "[S] Screen Markers",
        description = "Enable drawing of screen markers on top of the client, created by azotize",
        tags = {"boxes", "overlay", "panel"},
        conflicts = "Screen Markers"
)
public class azScreenMarkerPlugin extends Plugin {
    private static final String PLUGIN_NAME = "Screen Markers";

    private static final String CONFIG_GROUP = "screenmarkers";

    private static final String CONFIG_KEY = "markers";

    private static final String ICON_FILE = "panel_icon.png";

    private static final String DEFAULT_MARKER_NAME = "Marker";

    private static final Dimension DEFAULT_SIZE = new Dimension(2, 2);

    private final List<ScreenMarkerOverlay> screenMarkers = new ArrayList<>();

    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ScreenMarkerCreationOverlay overlay;

    @Inject
    private Gson gson;

    @Inject
    private ColorPickerManager colorPickerManager;

    @Inject
    private ScreenMarkerWidgetHighlightOverlay widgetHighlight;

    @Inject
    private azScreenMarkerConfig config;

    private ScreenMarkerMouseListener mouseListener;

    private ScreenMarkerPluginPanel pluginPanel;

    private NavigationButton navigationButton;

    private ScreenMarker currentMarker;

    public List<ScreenMarkerOverlay> getScreenMarkers() {
        return this.screenMarkers;
    }

    public Client getClient() {
        return this.client;
    }

    public ColorPickerManager getColorPickerManager() {
        return this.colorPickerManager;
    }

    public azScreenMarkerConfig getConfig() {
        return this.config;
    }

    @Provides
    azScreenMarkerConfig provideConfig(ConfigManager configManager) {
        return (azScreenMarkerConfig)configManager.getConfig(azScreenMarkerConfig.class);
    }

    ScreenMarker getCurrentMarker() {
        return this.currentMarker;
    }

    private boolean creatingScreenMarker = false;

    public boolean isCreatingScreenMarker() {
        return this.creatingScreenMarker;
    }

    public void setCreatingScreenMarker(boolean creatingScreenMarker) {
        this.creatingScreenMarker = creatingScreenMarker;
    }

    private boolean drawingScreenMarker = false;

    public boolean isDrawingScreenMarker() {
        return this.drawingScreenMarker;
    }

    private Rectangle selectedWidgetBounds = null;

    public Rectangle getSelectedWidgetBounds() {
        return this.selectedWidgetBounds;
    }

    public void setSelectedWidgetBounds(Rectangle selectedWidgetBounds) {
        this.selectedWidgetBounds = selectedWidgetBounds;
    }

    private Point startLocation = null;

    protected void startUp() throws Exception {
        loadCoxIds();
        this.warnedOfFail = false;
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.widgetHighlight);
        Objects.requireNonNull(this.screenMarkers);
        loadConfig(this.configManager.getConfiguration("screenmarkers", "markers")).forEach(this.screenMarkers::add);
        Objects.requireNonNull(this.overlayManager);
        this.screenMarkers.forEach(this.overlayManager::add);
        for (ScreenMarkerOverlay o : this.screenMarkers)
            o.setClient(this);
        this.pluginPanel = new ScreenMarkerPluginPanel(this);
        this.pluginPanel.rebuild();
        BufferedImage icon = ImageUtil.loadImageResource(getClass(), "panel_icon.png");
        this

                .navigationButton = NavigationButton.builder().tooltip("Screen Markers").icon(icon).priority(5).panel((PluginPanel)this.pluginPanel).build();
        this.clientToolbar.addNavigation(this.navigationButton);
        this.mouseListener = new ScreenMarkerMouseListener(this);
    }

    private ArrayList<Integer> coxIds = new ArrayList<>();
    public ArrayList<Integer> getCoxIds() {return this.coxIds;}

    private ArrayList<Integer> coxBoardIds = new ArrayList<>();
    public ArrayList<Integer> getCoxBoardIds() {return this.coxBoardIds;}

    boolean warnedOfFail = false;

    private void loadCoxIds() {
        try {
            for (String s : this.config.chestIds().split(",")) {
                Integer i = Integer.valueOf(s.trim());
                this.coxIds.add(i);
            }
        } catch (Exception e) {
            if (!this.warnedOfFail) {
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Screen Marker Chest Ids syntax fail. Check config is correct.", "");
                this.warnedOfFail = true;
            }
        }

        try {
            for (String s : this.config.boardIds().split(",")) {
                Integer i = Integer.valueOf(s.trim());
                this.coxBoardIds.add(i);
            }
        } catch (Exception e) {
            if (!this.warnedOfFail) {
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Screen Marker Board Ids syntax fail. Check config is correct.", "");
                this.warnedOfFail = true;
            }
        }
    }

    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.widgetHighlight);
        Objects.requireNonNull(ScreenMarkerOverlay.class);
        this.overlayManager.removeIf(ScreenMarkerOverlay.class::isInstance);
        this.screenMarkers.clear();
        this.clientToolbar.removeNavigation(this.navigationButton);
        setMouseListenerEnabled(false);
        this.creatingScreenMarker = false;
        this.drawingScreenMarker = false;
        this.pluginPanel = null;
        this.currentMarker = null;
        this.mouseListener = null;
        this.navigationButton = null;
        this.selectedWidgetBounds = null;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (this.screenMarkers.isEmpty() && event.getGroup().equals("screenmarkers") && event.getKey().equals("markers")) {
            Objects.requireNonNull(this.screenMarkers);
            loadConfig(event.getNewValue()).forEach(this.screenMarkers::add);
            Objects.requireNonNull(ScreenMarkerOverlay.class);
            this.overlayManager.removeIf(ScreenMarkerOverlay.class::isInstance);
            Objects.requireNonNull(this.overlayManager);
            this.screenMarkers.forEach(this.overlayManager::add);
        }
    }

    public void setMouseListenerEnabled(boolean enabled) {
        if (enabled) {
            this.mouseManager.registerMouseListener((MouseListener)this.mouseListener);
        } else {
            this.mouseManager.unregisterMouseListener((MouseListener)this.mouseListener);
        }
    }

    public void startCreation(Point location) {
        startCreation(location, DEFAULT_SIZE);
        if (this.selectedWidgetBounds == null)
            this.drawingScreenMarker = true;
    }

    public void startCreation(Point location, Dimension size) {
        this.currentMarker = new ScreenMarker(Instant.now().toEpochMilli(), "Marker " + (this.screenMarkers.size() + 1), this.pluginPanel.getSelectedBorderThickness(), this.pluginPanel.getSelectedColor(), this.pluginPanel.getSelectedFillColor(), true);
        this.startLocation = location;
        this.overlay.setPreferredLocation(location);
        this.overlay.setPreferredSize(size);
    }

    public void finishCreation(boolean aborted) {
        ScreenMarker marker = this.currentMarker;
        if (!aborted && marker != null) {
            ScreenMarkerOverlay screenMarkerOverlay = new ScreenMarkerOverlay(marker);
            screenMarkerOverlay.setClient(this);
            screenMarkerOverlay.setPreferredLocation(this.overlay.getBounds().getLocation());
            screenMarkerOverlay.setPreferredSize(this.overlay.getBounds().getSize());
            this.screenMarkers.add(screenMarkerOverlay);
            this.overlayManager.saveOverlay(screenMarkerOverlay);
            this.overlayManager.add(screenMarkerOverlay);
            this.pluginPanel.rebuild();
            updateConfig();
        }
        this.creatingScreenMarker = false;
        this.drawingScreenMarker = false;
        this.selectedWidgetBounds = null;
        this.startLocation = null;
        this.currentMarker = null;
        setMouseListenerEnabled(false);
        this.pluginPanel.setCreation(false);
    }

    public void completeSelection() {
        this.pluginPanel.getCreationPanel().unlockConfirm();
    }

    public void deleteMarker(ScreenMarkerOverlay marker) {
        this.screenMarkers.remove(marker);
        this.overlayManager.remove(marker);
        this.overlayManager.resetOverlay(marker);
        this.pluginPanel.rebuild();
        updateConfig();
    }

    void resizeMarker(Point point) {
        this.drawingScreenMarker = true;
        Rectangle bounds = new Rectangle(this.startLocation);
        bounds.add(point);
        this.overlay.setPreferredLocation(bounds.getLocation());
        this.overlay.setPreferredSize(bounds.getSize());
    }

    public void updateConfig() {
        if (this.screenMarkers.isEmpty()) {
            this.configManager.unsetConfiguration("screenmarkers", "markers");
            return;
        }
        String json = this.gson.toJson(this.screenMarkers.stream().map(ScreenMarkerOverlay::getMarker).collect(Collectors.toList()));
        this.configManager.setConfiguration("screenmarkers", "markers", json);
    }

    private Stream<ScreenMarkerOverlay> loadConfig(String json) {
        if (Strings.isNullOrEmpty(json))
            return Stream.empty();
        List<ScreenMarker> screenMarkerData = this.gson.fromJson(json, (new TypeToken<ArrayList<ScreenMarker>>() {

        }).getType());
        return screenMarkerData.stream().filter(Objects::nonNull).map(ScreenMarkerOverlay::new);
    }
}
