package io.papermc.paper.commands;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICommandListener;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class DelegatingCommandSource implements ICommandListener {

    private final ICommandListener delegate;

    public DelegatingCommandSource(ICommandListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void sendMessage(IChatBaseComponent message, UUID sender) {
        delegate.sendMessage(message, sender);
    }

    @Override
    public boolean shouldSendSuccess() {
        return delegate.shouldSendSuccess();
    }

    @Override
    public boolean shouldSendFailure() {
        return delegate.shouldSendFailure();
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return delegate.shouldBroadcastCommands();
    }

    @Override
    public CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
        return delegate.getBukkitSender(wrapper);
    }
}
