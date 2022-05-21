package net.runelite.client.plugins.socket.plugins.socketping;

import com.google.common.collect.ImmutableMap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.RaveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SocketPingOverlay extends Overlay {
    private static final Logger log = LoggerFactory.getLogger(SocketPingOverlay.class);

    final SocketPing plugin;

    final SocketPingConfig config;

    private boolean isHotkeyPressed = false;

    public boolean isHotkeyPressed() {
        return isHotkeyPressed;
    }

    public void setHotkeyPressed(boolean isHotkeyPressed) {
        this.isHotkeyPressed = isHotkeyPressed;
    }

    private boolean hotkeyToggle = false;

    private boolean isLeftMousePressed = false;

    public boolean isLeftMousePressed() {
        return isLeftMousePressed;
    }

    private Point lastMousePosition = null;

    private boolean openRadial = false;

    private long lastLeftMousePressedMillis = 0L;

    private KeyEvent keyheld = null;

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    public void setKeyheld(KeyEvent keyheld) {
        this.keyheld = keyheld;
    }

    public KeyEvent getKeyheld() {
        return keyheld;
    }

    final ArrayList<RadialWheelWidget> pingWidgets = new ArrayList<>();

    private static final ImmutableMap<PingType, ConcurrentHashMap<Object, Integer>> highlightedObjects;

    private static final HashMap<PingType, Supplier<Color>> colorMapping;

    public static ImmutableMap<PingType, ConcurrentHashMap<Object, Integer>> getHighlightedObjects() {
        return highlightedObjects;
    }

    private static final Color RADIAL_COLOR = new Color(0, 135, 255, 255);

    private static final int FADE_START_TICKS = 50;

    private static final long MOUSE_DEBOUNCE_MILLIS = 150L;

    private static final BufferedImage WARN_IMAGE;
    private static final BufferedImage OMW_IMAGE;
    private static final BufferedImage ASSIST_ME_IMAGE;
    private static final BufferedImage QUESTION_MARK_IMAGE;
    private static final BufferedImage PING_IMAGE;

    private final RaveUtils raveUtils;

    @Inject
    private Client client;

    static {
        colorMapping = new HashMap<>();
        highlightedObjects = ImmutableMap.of(PingType.TARGET, new ConcurrentHashMap<>(), PingType.WARN, new ConcurrentHashMap<>(), PingType.OMW, new ConcurrentHashMap<>(), PingType.ASSIST_ME, new ConcurrentHashMap<>(), PingType.QUESTION_MARK, new ConcurrentHashMap<>());
        WARN_IMAGE = ImageUtil.loadImageResource(SocketPingOverlay.class, "Danger.png");
        OMW_IMAGE = ImageUtil.loadImageResource(SocketPingOverlay.class, "OMW.png");
        ASSIST_ME_IMAGE = ImageUtil.loadImageResource(SocketPingOverlay.class, "Assist.png");
        QUESTION_MARK_IMAGE = ImageUtil.loadImageResource(SocketPingOverlay.class, "Missing.png");
        PING_IMAGE = ImageUtil.loadImageResource(SocketPingOverlay.class, "Ping.png");
    }

    @Inject
    SocketPingOverlay(SocketPing plugin, SocketPingConfig config, final RaveUtils raveUtils) {
        this.plugin = plugin;
        this.config = config;
        this.raveUtils = raveUtils;
        Objects.requireNonNull(config);
        colorMapping.put(PingType.TARGET, config::targetColor);
        Objects.requireNonNull(config);
        colorMapping.put(PingType.WARN, config::warnColor);
        Objects.requireNonNull(config);
        colorMapping.put(PingType.OMW, config::omwColor);
        Objects.requireNonNull(config);
        colorMapping.put(PingType.ASSIST_ME, config::assistMeColor);
        Objects.requireNonNull(config);
        colorMapping.put(PingType.QUESTION_MARK, config::questionMarkColor);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGHEST);
        setPosition(OverlayPosition.DYNAMIC);
        RadialWheelWidget pingWidget = new RadialWheelWidget(55, 15, 0.25D);
        RadialWheelWidget finalPingWidget = pingWidget;
        pingWidget.setUpdateFunction(point -> finalPingWidget.setLocation(new Point(point.getX(), point.getY())));
        pingWidget.setFunction(() -> plugin.ping(PingType.ASSIST_ME));
        pingWidget.setColor(RADIAL_COLOR);
        pingWidget.setBufferedImage(ASSIST_ME_IMAGE);
        pingWidget.setPictureLocationOffset(new Point(0, 35));
        pingWidgets.add(pingWidget);
        pingWidget = new RadialWheelWidget(55, 15, 0.75D);
        RadialWheelWidget finalPingWidget1 = pingWidget;
        pingWidget.setUpdateFunction(point -> finalPingWidget1.setLocation(new Point(point.getX(), point.getY())));
        pingWidget.setFunction(() -> plugin.ping(PingType.QUESTION_MARK));
        pingWidget.setColor(RADIAL_COLOR);
        pingWidget.setBufferedImage(QUESTION_MARK_IMAGE);
        pingWidget.setPictureLocationOffset(new Point(-35, 0));
        pingWidgets.add(pingWidget);
        pingWidget = new RadialWheelWidget(55, 15, 1.25D);
        RadialWheelWidget finalPingWidget2 = pingWidget;
        pingWidget.setUpdateFunction(point -> finalPingWidget2.setLocation(new Point(point.getX(), point.getY())));
        pingWidget.setFunction(() -> plugin.ping(PingType.WARN));
        pingWidget.setColor(RADIAL_COLOR);
        pingWidget.setBufferedImage(WARN_IMAGE);
        pingWidget.setPictureLocationOffset(new Point(0, -35));
        pingWidgets.add(pingWidget);
        pingWidget = new RadialWheelWidget(55, 15, 1.75D);
        RadialWheelWidget finalPingWidget3 = pingWidget;
        pingWidget.setUpdateFunction(point -> finalPingWidget3.setLocation(new Point(point.getX(), point.getY())));
        pingWidget.setFunction(() -> plugin.ping(PingType.OMW));
        pingWidget.setColor(RADIAL_COLOR);
        pingWidget.setBufferedImage(OMW_IMAGE);
        pingWidget.setPictureLocationOffset(new Point(35, 0));
        pingWidgets.add(pingWidget);
    }

    public Dimension render(Graphics2D graphics) {
        Point mousePosition = client.getMouseCanvasPosition();
        if (isHotkeyPressed || (keyheld != null && (config.hotkeyOmw().matches(keyheld) || config.hotkeyQuestionMark().matches(keyheld)
                || config.hotkeyAssistMe().matches(keyheld) || config.hotkeyWarn().matches(keyheld)))) {
            Color color = config.targetColor();
            if (keyheld != null) {
                if (config.hotkeyOmw().matches(keyheld))
                    color = config.omwColor();
                if (config.hotkeyQuestionMark().matches(keyheld))
                    color = config.questionMarkColor();
                if (config.hotkeyAssistMe().matches(keyheld))
                    color = config.assistMeColor();
                if (config.hotkeyWarn().matches(keyheld))
                    color = config.warnColor();
            }
            int circleSize = 10;
            graphics.setColor(color);
            graphics.fillOval(mousePosition.getX() - circleSize / 2, mousePosition.getY() - circleSize / 2, circleSize, circleSize);
        }
        if (lastMousePosition != null) {
            if (isLeftMousePressed && mousePosition.distanceTo(lastMousePosition) > 3)
                openRadial = true;
            if (openRadial)
                for (RadialWheelWidget pingWidget : pingWidgets) {
                    pingWidget.setSelected(pingWidget.isContainedIn(mousePosition));
                    pingWidget.updateLocation(lastMousePosition);
                    Color wheelColor = RADIAL_COLOR;
                    if (config.raveWheel() == SocketPingConfig.RaveMode.RAVE) {
                        wheelColor = plugin.raveColor;
                    } else if (config.raveWheel() == SocketPingConfig.RaveMode.EPILEPSY) {
                        wheelColor = Color.getHSBColor(new Random().nextFloat(), 1.0F, 1.0F);
                    } else if (config.raveWheel() == SocketPingConfig.RaveMode.FLOW) {
                        wheelColor = raveUtils.getColor(100, client.getGameCycle(), false);
                    }
                    pingWidget.setColor(wheelColor);
                    pingWidget.draw(graphics);
                }
        }
        highlightObjects(graphics);
        for (PingType pingType : highlightedObjects.keySet()) {
            ConcurrentHashMap<Object, Integer> objects = highlightedObjects.get(pingType);
            for (Object o : objects.keySet())
                objects.computeIfPresent(o, (k, v) -> v - 1);
            (highlightedObjects.get(pingType)).entrySet().removeIf(entry -> (entry.getValue() < 1));
        }
        return null;
    }

    private void highlightObjects(Graphics2D graphics) {
        for (PingType pingType : highlightedObjects.keySet()) {
            Color color = colorMapping.getOrDefault(pingType, () -> Color.black).get();
            ConcurrentHashMap<Object, Integer> objects = highlightedObjects.get(pingType);
            for (Object o : objects.keySet()) {
                WorldPoint wp = null;
                if (o instanceof NPC) {
                    highlight((NPC) o, objects.get(o), color);
                    wp = ((NPC) o).getWorldLocation();
                } else if (o instanceof GameObject) {
                    highlight((GameObject) o, objects.get(o), color);
                    wp = ((GameObject) o).getWorldLocation();
                } else if (o instanceof Player) {
                    highlight((Player) o, objects.get(o), color);
                    wp = ((Player) o).getWorldLocation();
                } else if (o instanceof WorldPoint) {
                    highlight(graphics, (WorldPoint) o, objects.get(o), color);
                    wp = (WorldPoint) o;
                }

                if (config.pingLocationImage() && wp != null) {
                    LocalPoint lp = LocalPoint.fromWorld(client, wp);
                    if (lp != null) {
                        BufferedImage img;
                        if (pingType.type.equals("Warn")) {
                            img = WARN_IMAGE;
                        } else if (pingType.type.equals("OMW")) {
                            img = OMW_IMAGE;
                        } else if (pingType.type.equals("Assist Me")) {
                            img = ASSIST_ME_IMAGE;
                        } else if (pingType.type.equals("Question Mark")) {
                            img = QUESTION_MARK_IMAGE;
                        } else {
                            img = PING_IMAGE;
                        }

                        Point base = Perspective.localToCanvas(client, lp, client.getPlane(), 50);
                        if (base != null) {
                            graphics.drawImage(img, base.getX() - 20, base.getY() - 10, null);
                        }
                    }
                }
            }
        }
    }

    private void highlight(GameObject gameObject, int tick, Color color) {
        float alpha = Math.max(0.0F, Math.min(tick, 50) / 50.0F);
        modelOutlineRenderer.drawOutline((TileObject)gameObject, config.overlayBorderWidth(), new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255.0F)), config.overlayBorderWidth());
    }

    private void highlight(NPC npc, int tick, Color color) {
        float alpha = Math.max(0.0F, Math.min(tick, 50) / 50.0F);
        modelOutlineRenderer.drawOutline(npc, config.overlayBorderWidth(), new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255.0F)), config.overlayBorderWidth());
    }

    private void highlight(Player player, int tick, Color color) {
        float alpha = Math.max(0.0F, Math.min(tick, 50) / 50.0F);
        modelOutlineRenderer.drawOutline(player, config.overlayBorderWidth(), new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255.0F)), config.overlayBorderWidth());
    }

    private void highlight(Graphics2D graphics, WorldPoint worldPoint, int tick, Color color) {
        LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
        if (lp != null) {
            Polygon polygon = Perspective.getCanvasTilePoly(client, lp);
            float alpha = Math.max(0.0F, Math.min(tick, 50) / 50.0F);
            graphics.setColor(new Color(0, 0, 0, (int)(20.0F * alpha)));
            graphics.fill(polygon);
            graphics.setStroke(new BasicStroke(config.tileBorderWidth()));
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255.0F)));
            graphics.draw(polygon);
        }
    }

    public void hotkeyPressed() {
        isHotkeyPressed = true;
    }

    public void hotkeyReleased() {
        if (config.hotkeyTrigger() == SocketPingConfig.HotkeyTrigger.HOLD) {
            isHotkeyPressed = false;
        } else if (config.hotkeyTrigger() == SocketPingConfig.HotkeyTrigger.TOGGLE) {
            hotkeyToggle = !hotkeyToggle;
            isHotkeyPressed = (hotkeyToggle && isHotkeyPressed);
        }
    }

    public MouseEvent leftMousePressed(MouseEvent mouseEvent) {
        if (isHotkeyPressed && mouseEvent.getButton() == 1) {
            Rectangle rectangle = new Rectangle(client.getViewportXOffset(), client.getViewportYOffset(), client.getViewportWidth(), client.getViewportHeight());
            java.awt.Point mousePoint = new java.awt.Point(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY());
            if (rectangle.contains(mousePoint)) {
                if (!isLeftMousePressed) {
                    lastMousePosition = client.getMouseCanvasPosition();
                }

                isLeftMousePressed = true;
                lastLeftMousePressedMillis = System.currentTimeMillis();
            }
        }
        return mouseEvent;
    }

    public MouseEvent leftMouseReleased(MouseEvent mouseEvent) {
        if (isLeftMousePressed && mouseEvent.getButton() == 1 && !client.isMenuOpen()) {
            Rectangle rectangle = new Rectangle(client.getViewportXOffset(), client.getViewportYOffset(), client.getViewportWidth(), client.getViewportHeight());
            java.awt.Point mousePoint = new java.awt.Point(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY());
            if (rectangle.contains(mousePoint)) {
                isLeftMousePressed = false;
                for (RadialWheelWidget pingWidget : pingWidgets) {
                    pingWidget.updateLocation(lastMousePosition);
                    if (pingWidget.isContainedIn(client.getMouseCanvasPosition())) {
                        pingWidget.getFunction().run();
                        reset();
                        mouseEvent.consume();
                        return mouseEvent;
                    }
                }
                if (keyheld != null) {
                    if (config.hotkeyWarn().matches(keyheld)) {
                        plugin.ping(PingType.WARN);
                        reset();
                        mouseEvent.consume();
                        return mouseEvent;
                    }
                    if (config.hotkeyAssistMe().matches(keyheld)) {
                        plugin.ping(PingType.ASSIST_ME);
                        reset();
                        mouseEvent.consume();
                        return mouseEvent;
                    }
                    if (config.hotkeyQuestionMark().matches(keyheld)) {
                        plugin.ping(PingType.QUESTION_MARK);
                        reset();
                        mouseEvent.consume();
                        return mouseEvent;
                    }
                    if (config.hotkeyOmw().matches(keyheld)) {
                        plugin.ping(PingType.OMW);
                        reset();
                        mouseEvent.consume();
                        return mouseEvent;
                    }
                }
                if (System.currentTimeMillis() - lastLeftMousePressedMillis < 150L)
                    plugin.ping(PingType.TARGET);
                reset();
                mouseEvent.consume();
                return mouseEvent;
            }
        }
        return mouseEvent;
    }

    public void reset() {
        if (config.hotkeyTrigger() == SocketPingConfig.HotkeyTrigger.TOGGLE) {
            hotkeyToggle = false;
            isHotkeyPressed = false;
        }
        keyheld = null;
        openRadial = false;
        lastMousePosition = null;
    }
}