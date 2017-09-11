package co.bugg.quickplay.gui.button;

import co.bugg.quickplay.Game;
import co.bugg.quickplay.Icons;
import co.bugg.quickplay.QuickPlay;
import co.bugg.quickplay.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Extension of GuiButton that allows for 64x64 buttons (or any size really)
 * with a custom texture background. Used for the main GUI buttons
 */
@ParametersAreNonnullByDefault
public class GameButton extends GuiButton {

    /**
     * The game this button is for
     */
    private Game game;

    public GameButton(int buttonId, int x, int y, Game game) {
        super(buttonId, x, y, Icons.iconWidth, Icons.iconHeight, "");
        this.game = game;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if(this.visible) {
            mc.renderEngine.bindTexture(QuickPlay.icons.get(this.game.fileID));
            drawTexturedModalRect(xPosition, yPosition, this.game.xStart, this.game.yStart, Icons.iconWidth, Icons.iconHeight);

            // Draw the string as well
            drawCenteredString(Minecraft.getMinecraft().fontRendererObj, game.name, xPosition + Icons.iconWidth / 2, yPosition + Icons.iconHeight + 2, QuickPlay.configManager.getConfig().colors.get("primary").getRGB());
            GlUtil.resetGlColor();
        }
    }

    public Game getGame() {
        return game;
    }
}
