package net.runelite.client.plugins.socket.plugins.socketDPS.table;
import java.awt.Color;

public class SocketTableElement {
    SocketTableAlignment alignment;

    Color color;

    String content;

    public void setAlignment(SocketTableAlignment alignment) {
        this.alignment = alignment;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SocketTableElement))
            return false;
        SocketTableElement other = (SocketTableElement)o;
        if (!other.canEqual(this))
            return false;
        Object this$alignment = getAlignment(), other$alignment = other.getAlignment();
        if ((this$alignment == null) ? (other$alignment != null) : !this$alignment.equals(other$alignment))
            return false;
        Object this$color = getColor(), other$color = other.getColor();
        if ((this$color == null) ? (other$color != null) : !this$color.equals(other$color))
            return false;
        Object this$content = getContent(), other$content = other.getContent();
        return !((this$content == null) ? (other$content != null) : !this$content.equals(other$content));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SocketTableElement;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $alignment = getAlignment();
        result = result * 59 + (($alignment == null) ? 43 : $alignment.hashCode());
        Object $color = getColor();
        result = result * 59 + (($color == null) ? 43 : $color.hashCode());
        Object $content = getContent();
        return result * 59 + (($content == null) ? 43 : $content.hashCode());
    }

    public String toString() {
        return "SocketTableElement(alignment=" + getAlignment() + ", color=" + getColor() + ", content=" + getContent() + ")";
    }

    SocketTableElement(SocketTableAlignment alignment, Color color, String content) {
        this.alignment = alignment;
        this.color = color;
        this.content = content;
    }

    public static SocketTableElementBuilder builder() {
        return new SocketTableElementBuilder();
    }

    public static class SocketTableElementBuilder {
        private SocketTableAlignment alignment;

        private Color color;

        private String content;

        public SocketTableElementBuilder alignment(SocketTableAlignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public SocketTableElementBuilder color(Color color) {
            this.color = color;
            return this;
        }

        public SocketTableElementBuilder content(String content) {
            this.content = content;
            return this;
        }

        public SocketTableElement build() {
            return new SocketTableElement(this.alignment, this.color, this.content);
        }

        public String toString() {
            return "SocketTableElement.SocketTableElementBuilder(alignment=" + this.alignment + ", color=" + this.color + ", content=" + this.content + ")";
        }
    }

    public SocketTableAlignment getAlignment() {
        return this.alignment;
    }

    public Color getColor() {
        return this.color;
    }

    public String getContent() {
        return this.content;
    }
}
