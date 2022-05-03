package me.sakuratao.cps_watcher;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.Setter;
import me.sakuratao.cps_watcher.command.MainCommand;
import me.sakuratao.cps_watcher.data.PlayerDataManager;
import me.sakuratao.cps_watcher.listener.PacketListener;
import me.sakuratao.cps_watcher.listener.PlayerListener;
import me.sakuratao.cps_watcher.message.MessageManager;
import me.sakuratao.cps_watcher.task.RP;
import me.sakuratao.cps_watcher.task.TA;
import me.sakuratao.cps_watcher.utils.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CPS_Watcher extends JavaPlugin {

    @Getter
    private final MessageManager messageManager = new MessageManager();

    @Getter @Setter
    private PlayerDataManager playerDataManager;

    private static CPS_Watcher watcher;
    private ProtocolManager protocolManager;
    private PacketListener packetListener;

    @Override
    public void onEnable() {
        // Plugin startup logic

        packetListener = new PacketListener(this, this, PacketType.Play.Client.ARM_ANIMATION);
        playerDataManager = new PlayerDataManager();
        watcher = this;
        messageManager.init(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(packetListener);

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);

        getCommand("cps").setExecutor(new MainCommand(this));

        TaskUtil.taskTimerAsync(new TA(this), 0, 20);
        TaskUtil.taskTimerAsync(new RP(this), 0, 21);

        getLogger().info("Made by: SakuraTao");

        for (Player p : Bukkit.getOnlinePlayers()){
            playerDataManager.create(p);
        }

    }

    @Override
    public void onDisable() {
        protocolManager.removePacketListener(packetListener);
    }

    public static CPS_Watcher getWatcher() {
        return watcher;
    }
}