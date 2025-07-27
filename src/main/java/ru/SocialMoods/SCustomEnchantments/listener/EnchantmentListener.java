package ru.SocialMoods.SCustomEnchantments.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.item.Item;
import ru.SocialMoods.SCustomEnchantments.api.EnchantmentAPI;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;
import ru.SocialMoods.SCustomEnchantments.service.EnchantmentService;
import ru.SocialMoods.SCustomEnchantments.utils.NBTUtils;
import ru.SocialMoods.SCustomEnchantments.utils.Utils;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@AllArgsConstructor
public class EnchantmentListener implements Listener {

    private final EnchantmentService enchantmentService;

    private final Random random = new Random();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Item tool = event.getItem();
        Block block = event.getBlock();

        enchantmentService.handleBlockBreak(tool, block, player);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            enchantmentService.handleEntityHit(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamageTaken(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            enchantmentService.handleDamageTaken(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnchant(EnchantItemEvent event) {
        if (event.isCancelled()) return;

        Item inputItem = event.getOldItem();
        Item outputItem = event.getNewItem();
        Player player = event.getEnchanter();

        Map<String, CustomEnchantment> possibleEnchants = EnchantmentAPI.getCompatibleEnchantments(inputItem)
                .stream()
                .collect(Collectors.toMap(
                        CustomEnchantment::getId,
                        e -> e,
                        (existing, replacement) -> existing
                ));

        if (possibleEnchants.isEmpty()) return;

        possibleEnchants.forEach((id, enchant) -> {
            float chance = Utils.calculateEnchantChance(enchant, player);
            if (random.nextFloat() < chance) {
                int level = Utils.calculateEnchantLevel(enchant);
                NBTUtils.addCustomEnchantment(outputItem, id, enchant.getDisplayName(), level);
            }
        });
    }
}