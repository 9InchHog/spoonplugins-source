package net.runelite.client.plugins.coxadditions.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.regex.Pattern;
import lombok.Setter;
import net.runelite.client.ui.overlay.RenderableEntity;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.Text;

@Setter
public class OrbTextComponent implements RenderableEntity
{
    private static final String COL_TAG_REGEX = "(<col=([0-9a-fA-F]){2,6}>)";
    private static final Pattern COL_TAG_PATTERN_W_LOOKAHEAD = Pattern.compile("(?=" + COL_TAG_REGEX + ")");

    private String text;
    private Point position = new Point();
    private Color color = Color.WHITE;
    private boolean outline;

    @Override
    public Dimension render(Graphics2D graphics)
    {
        final FontMetrics fontMetrics = graphics.getFontMetrics();

        if (COL_TAG_PATTERN_W_LOOKAHEAD.matcher(text).find())
        {
            final String[] parts = COL_TAG_PATTERN_W_LOOKAHEAD.split(text);
            int x = position.x;

            for (String textSplitOnCol : parts)
            {
                final String textWithoutCol = Text.removeTags(textSplitOnCol);
                final String colColor = textSplitOnCol.substring(textSplitOnCol.indexOf("=") + 1, textSplitOnCol.indexOf(">"));

                graphics.setColor(Color.BLACK);

                if (outline)
                {
                    graphics.drawString(textWithoutCol, x, position.y + 1);
                    graphics.drawString(textWithoutCol, x, position.y - 1);
                    graphics.drawString(textWithoutCol, x + 1, position.y);
                    graphics.drawString(textWithoutCol, x - 1, position.y);
                }
                else
                {
                    // shadow
                    graphics.drawString(textWithoutCol, x + 1, position.y + 1);
                }

                // actual text
                graphics.setColor(Color.decode("#" + colColor));
                graphics.drawString(textWithoutCol, x, position.y);

                x += fontMetrics.stringWidth(textWithoutCol);
            }
        }
        else
        {
            graphics.setColor(Color.BLACK);

            if (outline)
            {
                graphics.drawString(text, position.x, position.y + 1);
                graphics.drawString(text, position.x, position.y - 1);
                graphics.drawString(text, position.x + 1, position.y);
                graphics.drawString(text, position.x - 1, position.y);
            }
            else
            {
                // shadow
                graphics.drawString(text, position.x + 1, position.y + 1);
            }

            // actual text
            graphics.setColor(ColorUtil.colorWithAlpha(color, 0xFF));
            graphics.drawString(text, position.x, position.y);
        }

        return new Dimension(fontMetrics.stringWidth(text), fontMetrics.getHeight());
    }
}
