package co.bugg.quickplay.command.sub;

import co.bugg.quickplay.Reference;
import co.bugg.quickplay.command.QpBaseCommand;
import co.bugg.quickplay.command.QpSubCommand;
import co.bugg.quickplay.gui.MainColorGui;
import co.bugg.quickplay.util.TickDelay;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;

public class ColorCommand extends QpSubCommand {

    public ColorCommand(QpBaseCommand parent) {
        super(parent, "color", "Customize your " + Reference.MOD_NAME + " colors.", "");
    }

    @Override
    public void run(ICommandSender sender, String[] args) {
        new TickDelay(() -> Minecraft.getMinecraft().displayGuiScreen(new MainColorGui()), 2);
    }
}