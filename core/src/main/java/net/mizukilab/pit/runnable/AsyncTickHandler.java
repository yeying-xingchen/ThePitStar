package net.mizukilab.pit.runnable;

import cn.charlotte.pit.ThePit;
import cn.charlotte.pit.data.PlayerProfile;
import net.mizukilab.pit.PitHook;
import net.mizukilab.pit.actionbar.ActionBarManager;
import net.mizukilab.pit.data.operator.ProfileOperator;
import net.mizukilab.pit.item.factory.ItemFactory;
import net.mizukilab.pit.util.PublicUtil;
import nya.Skip;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Async tick handler
 */
@Skip
public class AsyncTickHandler extends BukkitRunnable {


    ThePit instance = ThePit.getInstance();

    private long tick = 0;

    public void flushIds() {
        PublicUtil.itemVersion = PitHook.getItemVersion();
        PublicUtil.signVer = PitHook.getGitVersion();
    }

    public AsyncTickHandler() {
        flushIds();
    }

    @Override
    public void run() {
        //trade
        ActionBarManager actionBarManager = (ActionBarManager) instance.getActionBarManager();
        if (actionBarManager != null) {
            actionBarManager.tick();
        }
        if (tick % 10 == 0) {
            //Async Lru Detector
            ItemFactory itemFactory = (ItemFactory) instance.getItemFactory();
            itemFactory.lru();
            flushIds();
        }
        if (++tick == Long.MIN_VALUE) {
            tick = 0; //从头开始
        }
        // 使用配置文件中的自动保存间隔
        int autoSaveInterval = instance.getGlobalConfig().getAutoSaveInterval();
        if (tick > 1200 && tick % autoSaveInterval == 0) {
            //AutoSave
            doAutoSave();
            return;
        }
        //Async Io Tracker
        ((ProfileOperator) instance.getProfileOperator()).tick();
    }

    public void doAutoSave() {
        final long last = System.currentTimeMillis();
        // 批量保存所有玩家资料，减少数据库操作次数
        instance.getProfileOperator().doSaveProfiles();

        final long now = System.currentTimeMillis();
        Bukkit.getLogger().info("Auto saved player backups, time: " + (now - last) + "ms");
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("pit.admin")) return;
            
            ((ProfileOperator) instance.getProfileOperator()).operatorStrict(player).ifPresent(operator -> {
                PlayerProfile profile = operator.profile();
                // 修复内存泄漏
                if (profile.getCombatTimer().hasExpired() && player.getLastDamageCause() != null) {
                    player.setLastDamageCause(null);
                }
                
                // 减少AntiAFK检查的频率，只在需要时更新
                final long lastActionTimestamp = profile.getLastActionTimestamp();
                if (now - lastActionTimestamp >= 10 * 60 * 1000) {
                    operator.pending(i -> {
                        profile.setLastActionTimestamp(now);
                    });
                }
            });
        });
    }
}
