package ru.SocialMoods.SCustomEnchantments.service;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;

public interface EnchantmentService {

    void handleBlockBreak(Item tool, Block block, Player player);

    void handleEntityHit(EntityDamageByEntityEvent event);

    void handleDamageTaken(EntityDamageEvent event);
}