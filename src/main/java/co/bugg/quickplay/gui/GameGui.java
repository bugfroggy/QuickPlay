package co.bugg.quickplay.gui;

import co.bugg.quickplay.Game;
import co.bugg.quickplay.Icons;
import co.bugg.quickplay.JoinLobby;
import co.bugg.quickplay.QuickPlay;
import co.bugg.quickplay.gui.button.ArrowButton;
import co.bugg.quickplay.gui.button.StarButton;
import co.bugg.quickplay.util.GameUtil;
import co.bugg.quickplay.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI screen for individual games, as opposed to the main GUI screen for the mod that lists all games
 */
public class GameGui extends QuickPlayGui {
    /**
     * Which game this GUI screen instance is for
     */
    Game game;
    /**
     * Whether or not this GUI is currently for customizing Party Mode settings
     */
    boolean partyMode = false;
    /**
     * HashMap mapping join button IDs to which join button they are
     */
    private HashMap<Integer, String> buttons = new HashMap<>();

    /**
     * The page of the main GUI that the user came from.
     * Is used by the back button to send them back to
     * the page they were just at.
     */
    private int cameFromPage = 1;

    public GameGui(Game game) {
        this.game = game;
    }

    public GameGui(Game game, int cameFromPage) {
        this.game = game;
        this.cameFromPage = cameFromPage;
    }

    public GameGui(Game game, int cameFromPage, boolean partyMode) {
        this.game = game;
        this.cameFromPage = cameFromPage;
        this.partyMode = partyMode;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        mc.renderEngine.bindTexture(QuickPlay.icons.get(this.game.fileID));
        // Draw this games icon at the top
        drawTexturedModalRect((width / 2 - Icons.iconWidth / 2), (height * 0.05f), this.game.xStart, this.game.yStart, Icons.iconWidth, Icons.iconHeight);

        // Draw the credits
        drawString(fontRenderer, QuickPlay.credit, width / 2 - fontRenderer.getStringWidth(QuickPlay.credit) / 2, height - 10, QuickPlay.configManager.getConfig().colors.get("primary").getRGB());
        GlUtil.resetGlColor();

        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton button : buttonList) {
            // If the button ends with ellipsis
            if(button.displayString.matches("^.*\\.\\.\\.$")) {
                if(mouseX > button.x && mouseX < button.x + button.width && mouseY > button.y && mouseY < button.y + button.height) {
                    List<String> hoverText = new ArrayList<>();
                    hoverText.add(buttons.get(button.id));
                    drawHoveringText(hoverText, mouseX, mouseY);
                }
            }
        }
    }

    /**
     * Create all the buttons!
     * It's lengthy!
     */
    @Override
    public void initGui() {
        super.initGui();

        // Get the number of buttons to render
        int buttonCount = game.commands != null ? game.commands.size() : 0;

        // Default sizes
        int defaultButtonWidth = 300;
        int defaultButtonHeight = 20;
        // Dynamic sizes (Change depending on number of buttons, which button is
        // currently being rendered, etc)
        int buttonWidth = defaultButtonWidth;
        // How far apart each button should be
        int buttonSpacing = 5;

        // Position of the lobby button
        int lobbyY = (int) ((height * 0.05) + Icons.iconHeight + 10);
        int lobbyX = (width / 2) - ((buttonWidth / 2));

        // What Y position dynamic (all but lobby) buttons are not created above
        int startingHeight = (int) ((height * 0.05) + Icons.iconHeight + 10 + defaultButtonHeight + buttonSpacing);

        // buttonId incremented by 1 every time a button is added
        int buttonId = 0;
        int trueHeight = height - startingHeight;
        // How many buttons can fit on the screen in one column
        int maxButtonPerColumn = trueHeight / (defaultButtonHeight + buttonSpacing);
        // How many columns are required to fit all the buttons in
        double columnCount = Math.ceil(((double)buttonCount / (double)maxButtonPerColumn));

        // When there's no buttons (besides the lobby button), columnCount is 0 which throws an error.
        if(columnCount < 1) {
            columnCount = 1;
        }

        // Where the first button is rendered on the X axis (the column furthest to the left)
        int startingX = (width / 2) - ((buttonWidth / 2) + (buttonSpacing * ((int) columnCount - 1) / 2));

        // Resizing button width depending on how many columns there are
        buttonWidth = buttonWidth / (int) columnCount;

        // Where to position the top left corner of the button
        int buttonY = startingHeight;
        int buttonX = startingX;

        // Create the back button
        buttonList.add(new ArrowButton(buttonId, (width / 2) - (Icons.iconWidth / 2 + ArrowButton.width + 5), (int) (height * 0.05) + Icons.iconHeight / 2 - ArrowButton.height / 2, 0));
        buttons.put(buttonId, null);
        buttonId++;

        // Set whether or not the star is toggled on
        // (i.e. whether this gamemode is the user's favorite)
        boolean starOn;
        starOn = game.isFavorite();

        // Star button should only be added if not party mode
        if(!partyMode) {
            // Create the star button
            buttonList.add(new StarButton(buttonId, (width / 2) + (Icons.iconWidth / 2 + 5), (int) (height * 0.05) + Icons.iconHeight / 2 - StarButton.height / 2, starOn));
            buttons.put(buttonId, null);
            buttonId++;
        }

        // Lobby button should only be added if not party mode
        if(!partyMode) {
            // Create the lobby button
            buttonList.add(new GuiButton(buttonId, lobbyX, lobbyY, defaultButtonWidth, defaultButtonHeight, game.lobbyButtonString));
            // Register the button's ID
            buttons.put(buttonId, null);
            buttonId++;
        }

        // if any play commands exist
        if(game.commands != null) {
            int currentColumn = 1;
            for (Map.Entry<String, String> entry : game.commands.entrySet()) {

                // Reached the end of the columns so go back to the beginning
                if(currentColumn > columnCount) {
                    currentColumn = 1;
                    buttonX = startingX;

                    // Move one button position lower
                    buttonY += (defaultButtonHeight + buttonSpacing);
                }

                String shortenedString = GameUtil.getTextWithEllipsis(buttonWidth, entry.getKey());

                // Whether check boxes or buttons should be displayed
                if(partyMode) {
                    // How wide each check box is in pixels
                    // Useful in (approximately) centering checkboxes
                    int checkBoxWidth = 13;

                    // Move the icon over a little bit, centering it in it's dedicated space
                    buttonX = buttonX + (buttonWidth / 2) - ((checkBoxWidth + fontRenderer.getStringWidth(shortenedString)) / 2);

                    buttonList.add(new GuiCheckBox(buttonId, buttonX, buttonY, shortenedString, QuickPlay.configManager.getConfig().enabledPartyCommands.contains(game.commands.get(entry.getKey()))));

                } else {
                    buttonList.add(new GuiButton(buttonId, buttonX, buttonY, buttonWidth, defaultButtonHeight, shortenedString));
                }
                // Register the button's ID
                buttons.put(buttonId, entry.getKey());
                buttonId++;

                currentColumn++;
                // Move one button position over
                buttonX += (buttonWidth + buttonSpacing);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if(partyMode) {
            switch(button.id) {
                // If the button is the back button
                case 0:
                    openGui(new PartyGui(cameFromPage));
                    break;
                default:
                    String command = game.commands.get(buttons.get(button.id));
                    QuickPlay.configManager.togglePartyCommand(command, ((GuiCheckBox) button).isChecked());
                    break;
            }
        } else {
            switch (button.id) {
                // If the button is the back button
                case 0:
                    openGui(new MainGui(cameFromPage));
                    break;

                // If the button is the star button
                case 1:
                    StarButton starButton = (StarButton) button;
                    starButton.on = !starButton.on;

                    if (starButton.on) {
                        QuickPlay.configManager.addFavorite(game);
                    } else {
                        QuickPlay.configManager.removeFavorite(game);
                    }

                    QuickPlay.configManager.saveConfig();
                    break;

                // If the button is the lobby button
                case 2:
                    // If the lobby string is a command
                    if (game.lobbyName.startsWith("/")) {
                        Minecraft.getMinecraft().player.sendChatMessage(game.lobbyName);
                    } else {
                        new JoinLobby(game.lobbyName, Minecraft.getMinecraft().player);
                    }
                    closeGui();
                    break;

                // Handle like a normal button
                default:
                    final String[] command = {game.commands.get(buttons.get(button.id))};

                    // If the game command is an actual command
                    if (command[0].startsWith("/")) {

                        final boolean[] clientCommand = {false};
                        // Check whether command is a mod command. If so then execute its client command
                        // instead of the command on the server
                        ClientCommandHandler.instance.getCommands().forEach((key, value) -> {
                            String parsedCommand = command[0];
                            StringBuilder builder = new StringBuilder(parsedCommand);

                            // Delete the slash from the beginning of the string
                            builder.deleteCharAt(0);
                            // Also delete any arguments
                            if (command[0].contains(" ")) {
                                builder.delete(command[0].indexOf(" ") - 1, command[0].length() - 1);
                            }

                            if (builder.toString().equals(key)) {
                                clientCommand[0] = true;
                            }
                        });

                        // If the command is a client command, then execute it as a client command
                        if (clientCommand[0]) {
                            ICommandSender sender = Minecraft.getMinecraft().player.getCommandSenderEntity();
                            if (sender != null) {
                                ClientCommandHandler.instance.executeCommand(sender, command[0].substring(1, command[0].length()));
                            }
                        } else {
                            Minecraft.getMinecraft().player.sendChatMessage(command[0]);
                        }
                    } else {
                        Minecraft.getMinecraft().player.sendChatMessage("/play " + command[0]);
                    }
                    closeGui();
                    break;
            }
        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        obeySettings();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
