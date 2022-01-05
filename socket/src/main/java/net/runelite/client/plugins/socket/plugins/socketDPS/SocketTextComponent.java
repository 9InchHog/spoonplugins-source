package net.runelite.client.plugins.socket.plugins.socketDPS;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.regex.Pattern;
import net.runelite.client.ui.overlay.RenderableEntity;

public class SocketTextComponent implements RenderableEntity {
    private static final String COL_TAG_REGEX = "(<col=([0-9a-fA-F]){2,6}>)";

    public void setText(String text) {
        this.text = text;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private static final Pattern COL_TAG_PATTERN_W_LOOKAHEAD = Pattern.compile("(?=(<col=([0-9a-fA-F]){2,6}>))");

    private static final Pattern COL_TAG_PATTERN = Pattern.compile("(<col=([0-9a-fA-F]){2,6}>)");

    private String text;

    private Point position = new Point();

    private Color color = Color.WHITE;

    public static String textWithoutColTags(String text) {
        return COL_TAG_PATTERN.matcher(text).replaceAll("");
    }

    public Dimension render(Graphics2D graphics) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        if (COL_TAG_PATTERN.matcher(this.text).find()) {
            String[] parts = COL_TAG_PATTERN_W_LOOKAHEAD.split(this.text);
            int x = this.position.x;
            for (String textSplitOnCol : parts) {
                String textWithoutCol = textWithoutColTags(textSplitOnCol);
                String colColor = textSplitOnCol.substring(textSplitOnCol.indexOf("=") + 1, textSplitOnCol.indexOf(">"));
                graphics.setColor(Color.BLACK);
                graphics.drawString(textWithoutCol, x + 1, this.position.y + 1);
                graphics.setColor(Color.decode("#" + colColor));
                graphics.drawString(textWithoutCol, x, this.position.y);
                x += fontMetrics.stringWidth(textWithoutCol);
            }
        } else {
            graphics.setColor(Color.BLACK);
            graphics.drawString(this.text, this.position.x + 1, this.position.y + 1);
            graphics.setColor(this.color);
            graphics.drawString(this.text, this.position.x, this.position.y);
        }
        return new Dimension(fontMetrics.stringWidth(this.text), fontMetrics.getHeight());
    }
}
