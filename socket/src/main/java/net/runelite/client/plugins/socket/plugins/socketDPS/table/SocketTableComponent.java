package net.runelite.client.plugins.socket.plugins.socketDPS.table;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.NonNull;
import net.runelite.client.plugins.socket.plugins.socketDPS.SocketTextComponent;
import net.runelite.client.ui.overlay.components.LayoutableRenderableEntity;
import net.runelite.client.ui.overlay.components.TextComponent;

public class SocketTableComponent implements LayoutableRenderableEntity {
    public void setDefaultAlignment(SocketTableAlignment defaultAlignment) {
        this.defaultAlignment = defaultAlignment;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setGutter(Dimension gutter) {
        this.gutter = gutter;
    }

    public void setPreferredLocation(Point preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    private static final SocketTableElement EMPTY_ELEMENT = SocketTableElement.builder().build();

    private final List<SocketTableElement> columns = new ArrayList<>();

    public List<SocketTableElement> getColumns() {
        return this.columns;
    }

    private final List<SocketTableRow> rows = new ArrayList<>();

    public List<SocketTableRow> getRows() {
        return this.rows;
    }

    private final Rectangle bounds = new Rectangle();

    public Rectangle getBounds() {
        return this.bounds;
    }

    private SocketTableAlignment defaultAlignment = SocketTableAlignment.LEFT;

    private Color defaultColor = Color.WHITE;

    private Dimension gutter = new Dimension(3, 0);

    private Point preferredLocation = new Point();

    private Dimension preferredSize = new Dimension(129, 0);

    public Dimension render(Graphics2D graphics) {
        FontMetrics metrics = graphics.getFontMetrics();
        SocketTableRow colRow = SocketTableRow.builder().elements(this.columns).build();
        int[] columnWidths = getColumnWidths(metrics, colRow);
        graphics.translate(this.preferredLocation.x, this.preferredLocation.y);
        int height = displayRow(graphics, colRow, 0, columnWidths, metrics);
        for (SocketTableRow row : this.rows)
            height = displayRow(graphics, row, height, columnWidths, metrics);
        graphics.translate(-this.preferredLocation.x, -this.preferredLocation.y);
        Dimension dimension = new Dimension(this.preferredSize.width, height);
        this.bounds.setLocation(this.preferredLocation);
        this.bounds.setSize(dimension);
        return dimension;
    }

    private int displayRow(Graphics2D graphics, SocketTableRow row, int height, int[] columnWidths, FontMetrics metrics) {
        int x = 0;
        int startingRowHeight = height;
        List<SocketTableElement> elements = row.getElements();
        for (int i = 0; i < elements.size(); i++) {
            int y = startingRowHeight;
            SocketTableElement cell = elements.get(i);
            String content = cell.getContent();
            if (content != null) {
                String[] lines = lineBreakText(content, columnWidths[i], metrics);
                SocketTableAlignment alignment = getCellAlignment(row, i);
                Color color = getCellColor(row, i);
                for (String line : lines) {
                    int alignmentOffset = getAlignedPosition(line, alignment, columnWidths[i], metrics);
                    TextComponent leftLineComponent = new TextComponent();
                    y += metrics.getHeight();
                    leftLineComponent.setPosition(new Point(x + alignmentOffset, y));
                    leftLineComponent.setText(line);
                    leftLineComponent.setColor(color);
                    leftLineComponent.render(graphics);
                }
                height = Math.max(height, y);
                x += columnWidths[i] + this.gutter.width;
            }
        }
        return height + this.gutter.height;
    }

    private int[] getColumnWidths(FontMetrics metrics, SocketTableRow columnRow) {
        int numCols = this.columns.size();
        for (SocketTableRow r : this.rows)
            numCols = Math.max(r.getElements().size(), numCols);
        int[] maxtextw = new int[numCols];
        int[] maxwordw = new int[numCols];
        boolean[] flex = new boolean[numCols];
        boolean[] wrap = new boolean[numCols];
        int[] finalcolw = new int[numCols];
        List<SocketTableRow> rows = new ArrayList<>(this.rows);
        rows.add(columnRow);
        for (SocketTableRow r : rows) {
            List<SocketTableElement> elements = r.getElements();
            for (int k = 0; k < elements.size(); k++) {
                SocketTableElement ele = elements.get(k);
                String cell = ele.getContent();
                if (cell != null) {
                    int cellWidth = getTextWidth(metrics, cell);
                    maxtextw[k] = Math.max(maxtextw[k], cellWidth);
                    for (String word : cell.split(" "))
                        maxwordw[k] = Math.max(maxwordw[k], getTextWidth(metrics, word));
                    if (maxtextw[k] == cellWidth)
                        wrap[k] = cell.contains(" ");
                }
            }
        }
        int left = this.preferredSize.width - (numCols - 1) * this.gutter.width;
        double avg = (left / numCols);
        int nflex = 0;
        int col;
        for (col = 0; col < numCols; col++) {
            double maxNonFlexLimit = 1.5D * avg;
            flex[col] = (maxtextw[col] > maxNonFlexLimit);
            if (flex[col]) {
                nflex++;
            } else {
                finalcolw[col] = maxtextw[col];
                left -= finalcolw[col];
            }
        }
        if (left < nflex * avg)
            for (col = 0; col < numCols; col++) {
                if (!flex[col] && wrap[col]) {
                    left += finalcolw[col];
                    finalcolw[col] = 0;
                    flex[col] = true;
                    nflex++;
                }
            }
        int tot = 0;
        int i;
        for (i = 0; i < numCols; i++) {
            if (flex[i]) {
                maxtextw[i] = Math.min(maxtextw[i], this.preferredSize.width);
                tot += maxtextw[i];
            }
        }
        for (i = 0; i < numCols; i++) {
            if (flex[i]) {
                finalcolw[i] = left * maxtextw[i] / tot;
                finalcolw[i] = Math.max(finalcolw[i], maxwordw[i]);
                left -= finalcolw[i];
            }
        }
        int extraPerCol = left / numCols;
        for (int j = 0; j < numCols; j++) {
            finalcolw[j] = finalcolw[j] + extraPerCol;
            left -= extraPerCol;
        }
        finalcolw[finalcolw.length - 1] = finalcolw[finalcolw.length - 1] + left;
        return finalcolw;
    }

    private static int getTextWidth(FontMetrics metrics, String cell) {
        return metrics.stringWidth(SocketTextComponent.textWithoutColTags(cell));
    }

    private static String[] lineBreakText(String text, int maxWidth, FontMetrics metrics) {
        String[] words = text.split(" ");
        if (words.length == 0)
            return new String[0];
        StringBuilder wrapped = new StringBuilder(words[0]);
        int spaceLeft = maxWidth - getTextWidth(metrics, wrapped.toString());
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            int wordLen = getTextWidth(metrics, word);
            int spaceWidth = metrics.stringWidth(" ");
            if (wordLen + spaceWidth > spaceLeft) {
                wrapped.append("\n").append(word);
                spaceLeft = maxWidth - wordLen;
            } else {
                wrapped.append(" ").append(word);
                spaceLeft -= spaceWidth + wordLen;
            }
        }
        return wrapped.toString().split("\n");
    }

    public boolean isEmpty() {
        return (this.columns.size() == 0 || this.rows.size() == 0);
    }

    private void ensureColumnSize(int size) {
        while (size > this.columns.size())
            this.columns.add(SocketTableElement.builder().build());
    }

    private static int getAlignedPosition(String str, SocketTableAlignment alignment, int columnWidth, FontMetrics metrics) {
        int stringWidth = getTextWidth(metrics, str);
        int offset = 0;
        switch (alignment) {
            case CENTER:
                offset = columnWidth / 2 - stringWidth / 2;
                break;
            case RIGHT:
                offset = columnWidth - stringWidth;
                break;
        }
        return offset;
    }

    private Color getCellColor(SocketTableRow row, int colIndex) {
        List<SocketTableElement> rowElements = row.getElements();
        SocketTableElement cell = (colIndex < rowElements.size()) ? rowElements.get(colIndex) : EMPTY_ELEMENT;
        SocketTableElement column = (colIndex < this.columns.size()) ? this.columns.get(colIndex) : EMPTY_ELEMENT;
        return firstNonNull(new Color[] { cell
                .getColor(), row
                .getRowColor(), column
                .getColor(), this.defaultColor });
    }

    public void setColumnAlignment(int col, SocketTableAlignment alignment) {
        assert this.columns.size() > col;
        ((SocketTableElement)this.columns.get(col)).setAlignment(alignment);
    }

    public void setColumnAlignments(@Nonnull SocketTableAlignment... alignments) {
        ensureColumnSize(alignments.length);
        for (int i = 0; i < alignments.length; i++)
            setColumnAlignment(i, alignments[i]);
    }

    private SocketTableAlignment getCellAlignment(SocketTableRow row, int colIndex) {
        List<SocketTableElement> rowElements = row.getElements();
        SocketTableElement cell = (colIndex < rowElements.size()) ? rowElements.get(colIndex) : EMPTY_ELEMENT;
        SocketTableElement column = (colIndex < this.columns.size()) ? this.columns.get(colIndex) : EMPTY_ELEMENT;
        return firstNonNull(new SocketTableAlignment[] { cell
                .getAlignment(), row
                .getRowAlignment(), column
                .getAlignment(), this.defaultAlignment });
    }

    @SafeVarargs
    private static <T> T firstNonNull(@Nullable T... elements) {
        if (elements == null || elements.length == 0)
            return null;
        int i = 0;
        T cur = elements[0];
        while (cur == null && i < elements.length) {
            cur = elements[i];
            i++;
        }
        return cur;
    }

    public void addRow(@Nonnull String... cells) {
        List<SocketTableElement> elements = new ArrayList<>();
        for (String cell : cells)
            elements.add(SocketTableElement.builder().content(cell).build());
        SocketTableRow row = SocketTableRow.builder().build();
        row.setElements(elements);
        this.rows.add(row);
    }

    public void addRows(@Nonnull String[]... rows) {
        for (String[] row : rows)
            addRow(row);
    }

    public void addRows(@NonNull SocketTableRow... rows) {
        if (rows == null)
            throw new NullPointerException("rows is marked @NonNull but is null");
        this.rows.addAll(Arrays.asList(rows));
    }

    public void setRows(@Nonnull String[]... elements) {
        this.rows.clear();
        addRows(elements);
    }

    public void setRows(@Nonnull SocketTableRow... elements) {
        this.rows.clear();
        this.rows.addAll(Arrays.asList(elements));
    }

    public void addColumn(@Nonnull String col) {
        this.columns.add(SocketTableElement.builder().content(col).build());
    }

    public void addColumns(@NonNull SocketTableElement... columns) {
        if (columns == null)
            throw new NullPointerException("columns is marked @NonNull but is null");
        this.columns.addAll(Arrays.asList(columns));
    }

    public void setColumns(@Nonnull SocketTableElement... elements) {
        this.columns.clear();
        this.columns.addAll(Arrays.asList(elements));
    }

    public void setColumns(@Nonnull String... columns) {
        this.columns.clear();
        for (String col : columns)
            addColumn(col);
    }
}
