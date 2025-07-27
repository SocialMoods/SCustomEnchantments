package ru.SocialMoods.SCustomEnchantments.service.impl;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import lombok.extern.slf4j.Slf4j;
import ru.SocialMoods.SCustomEnchantments.api.EnchantmentAPI;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;
import ru.SocialMoods.SCustomEnchantments.service.EnchantmentService;
import ru.SocialMoods.SCustomEnchantments.utils.NBTUtils;

import java.util.Map;

@Slf4j
public class EnchantmentServiceImpl implements EnchantmentService {

    @Override
    public void handleBlockBreak(Item tool, Block block, Player player) {
        Map<String, Integer> enchantments = NBTUtils.getCustomEnchantments(tool);

        enchantments.forEach((id, level) -> {
            CustomEnchantment enchantment = EnchantmentAPI.getEnchantment(id);
            if (enchantment != null && enchantment.isSupported(tool)) {
                enchantment.onBlockBreak(tool, block, player, level);
            }
        });
    }

    @Override
    public void handleEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        Item item = player.getInventory().getItemInHand();

        Map<String, Integer> enchantments = NBTUtils.getCustomEnchantments(item);

        enchantments.forEach((id, level) -> {
            CustomEnchantment enchantment = EnchantmentAPI.getEnchantment(id);
            if (enchantment != null && enchantment.isSupported(item)) {
                event.setCancelled(!enchantment.onHit(event));
            }
        });
    }

    @Override
    public void handleDamageTaken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        for (Item armor : player.getInventory().getArmorContents()) {
            Map<String, Integer> enchantments = NBTUtils.getCustomEnchantments(armor);

            enchantments.forEach((id, level) -> {
                CustomEnchantment enchantment = EnchantmentAPI.getEnchantment(id);
                if (enchantment != null && enchantment.isSupported(armor)) {
                    event.setCancelled(!enchantment.onDamage(event));
                }
            });
        }
    }
}