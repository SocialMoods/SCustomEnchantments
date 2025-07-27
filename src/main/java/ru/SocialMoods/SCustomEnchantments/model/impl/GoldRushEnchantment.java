package ru.SocialMoods.SCustomEnchantments.model.impl;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import lombok.extern.slf4j.Slf4j;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;

@Slf4j
public class GoldRushEnchantment extends CustomEnchantment {

    public GoldRushEnchantment() {
        this.id = "gold_rush";
        this.displayName = "Золотая лихорадка";
        this.rarity = Enchantment.Rarity.RARE;
    }

    @Override
    public boolean isSupported(Item item) {
        return item instanceof ItemTool && item.isPickaxe();
    }

    @Override
    public void onBlockBreak(Item tool, Block block, Player player, int level) {

        Block gold = Block.get(Block.GOLD_BLOCK);
        block.getLevel().setBlock(player, gold, true, true);

    }

    @Override
    public float getEnchantChance() {
        return 1f;
    }
}