package co.bugg.quickplay.command;

import co.bugg.quickplay.command.sub.*;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@ParametersAreNonnullByDefault
public class QpBaseCommand implements ICommand {

    public LinkedList<AbstractSubCommand> subCommands = new LinkedList<>();

    public QpBaseCommand() {
        subCommands.add(new HelpCommand(this));
        subCommands.add(new ConfigCommand(this));
        // subCommands.add(new ColorCommand(this));
        subCommands.add(new PartyCommand(this));
        subCommands.add(new LimboCommand(this));
        subCommands.add(new DebugCommand(this));
        subCommands.add(new DestroyConfigCommand(this));
    }

    @Override
    public String getCommandName() {
        return "qp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/qp <args...>";
    }

    @Override
    public List<String> getCommandAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("quickplay");
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length > 0) {
            AbstractSubCommand command = getCommand(args[0]);
            if(command != null) {
                command.run(sender, args);
            } else {
                // Command not found
                sender.addChatMessage(new ChatComponentTranslation("quickplay.command.unknown").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            }
        } else {
            // No args provided, run the help command if possible
            AbstractSubCommand helpCommand = getCommand("help");
            if(helpCommand != null) {
                helpCommand.run(sender, args);
            } else {
                sender.addChatMessage(new ChatComponentTranslation("quickplay.command.unknown").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> tabCompletions = new ArrayList<>();

        if(args.length > 1) {
            AbstractSubCommand command = getCommand(args[0]);
            if(command != null) {
                tabCompletions.addAll(command.getTabCompletions(sender, args, pos));
            }
        } else {
            for(AbstractSubCommand subCommand : this.subCommands) {
                if(subCommand.getName().startsWith(args[args.length - 1])) {
                    tabCompletions.add(subCommand.getName());
                }
            }
        }

        return tabCompletions;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    /**
     * Get the subcommand with the provided name
     * @param name Name of the sub command
     * @return Sub command, null if command doesn't exist
     */
    public AbstractSubCommand getCommand(String name) {
        for(AbstractSubCommand command : subCommands) {
            if(command.name.equalsIgnoreCase(name)) {
                return command;
            }
        }
        return null;
    }
}
