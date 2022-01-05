

package net.runelite.client.plugins.bobigsplits;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("bigsplits")
public interface BigSplitsConfig extends Config
{

	@ConfigSection(	name = "<html><font color=#00aeef>Tile options",description = "Configuration for tile options",	position = 0,	closedByDefault = false)
	public static final String tileSection = "Tile";

	@ConfigSection(
			name = "<html><font color=#00aeef>Tick options",description = "Configuration for tick options",position = 1,closedByDefault = false)
	public static final String tickSection = "Tick";




//TILE 3+
	@ConfigItem(position = 1,keyName = "tileColor",	name = "Tile color tick 3+",description = "Color of tile when above 3 ticks",	section = "Tile")
	@Alpha
	default Color tileColor()
	{
		return Color.YELLOW;
	}

	@Range(max = 255, min = 0)
	@ConfigItem(name = "Fill opacity",keyName = "tileFill",description = "",position = 2,section = "Tile")
	default int tileFill() 	{return 10;}

	@Range(max = 5, min = 0)
	@ConfigItem(name = "Tile thickness",keyName = "strokeSize",description = "Configure the thickness of the tile in pixels.",position = 3,section = "Tile")
	@Units("px")
	default int tileStroke() {return 2;	}



//TILE 2
	@ConfigItem(position = 4,keyName = "tileColor2",		name = "Tile color tick 2",description = "Color of tile when on tick 2",section = "Tile")
	@Alpha
	default Color TileColor2()
	{
		return Color.ORANGE;
	}

	@Range(max = 255, min = 0)
	@ConfigItem(name = "Fill opacity",keyName = "tileFill2",description = "",position =5,section = "Tile")
	default int tileFill2() 	{return 10;}

	@Range(max = 5, min = 0)
	@ConfigItem(name = "Tile thickness",keyName = "strokeSize2",description = "Configure the thickness of the tile in pixels.",position = 6,section = "Tile")
	@Units("px")
	default int tileStroke2() {return 2;}





//TILE 1
	@ConfigItem(position = 7,keyName = "tileColor1",		name = "Tile color tick 1",description = "Color of tile when on tick 1",section = "Tile")
	@Alpha
	default Color TileColor1() { return Color.RED;	}


	@Range(max = 255, min = 0)
	@ConfigItem(name = "Fill opacity",keyName = "tileFill1",description = "",position = 8,section = "Tile")
	default int tileFill1() 	{return 10;}

	@Range(max = 5, min = 0)
	@ConfigItem(name = "Tile thickness",keyName = "strokeSize1",description = "Configure the thickness of the tile in pixels.",position = 9,section = "Tile")
	@Units("px")
	default int tileStroke1() {return 2;	}






//TEXT 3+

	@ConfigItem(position = 10,keyName = "textColor",			name = "Text color tick 3+",description = "Color of the split timer when above 3",section = "Tick")
	@Alpha
	default Color textColor() 	{ return Color.WHITE;}


	@Range(max = 32, min = 0)
	@ConfigItem(name = "Text size tick 3+",keyName = "textSize",description = "Configure the size of text.",position = 11,section = "Tick")
	default int textSize() {return 12;}


//TEXT 2
	@ConfigItem(position = 12,keyName = "textColor2",		name = "Text color tick 2",description = "Color of the split timer when on tick 2",section = "Tick")
	@Alpha
	default Color textColor2() 	{ return Color.ORANGE;}

	@Range(max = 32, min = 0)
	@ConfigItem(name = "Text size tick 2",keyName = "textSize2",description = "Configure the size of text.",position = 13,section = "Tick")
	default int textSize2() {return 12;}


//TEXT 1
	@ConfigItem(position = 14,keyName = "textColor1",		name = "Text color tick 1",description = "Color of the split timer when on tick 1",	section = "Tick")
	@Alpha
	default Color textColor1()	{return Color.RED;}

	@Range(max = 32, min = 0)
	@ConfigItem(name = "Text size tick 1",keyName = "textSize1",description = "Configure the size of text.",position = 14,section = "Tick")
	default int textSize1() {return 12;}

//TEXT STYLE
	@Range(max = 2, min = 0)
	@ConfigItem(name = "Text style",keyName = "textStyle",description = "Regular:0, Bold:1, Italics:2",position = 15,section = "Tick")
	default int textStyle() {return 0;}


























}
