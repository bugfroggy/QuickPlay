package co.bugg.quickplay.command;

import co.bugg.quickplay.gui.MainColorGui;
import co.bugg.quickplay.util.TickDelay;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class QpColorCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "qpcolor";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/qpcolor";
    }

    @Override
    public List<String> getCommandAliases() {
        List<String> aliases = new ArrayList<>();

        aliases.add("qpc");

        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        new TickDelay(() -> Minecraft.getMinecraft().displayGuiScreen(new MainColorGui()), 2);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
