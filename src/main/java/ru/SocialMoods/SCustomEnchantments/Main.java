package ru.SocialMoods.SCustomEnchantments;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import ru.SocialMoods.SCustomEnchantments.api.EnchantmentAPI;
import ru.SocialMoods.SCustomEnchantments.command.EnchantCommand;
import ru.SocialMoods.SCustomEnchantments.listener.EnchantmentListener;
import ru.SocialMoods.SCustomEnchantments.model.impl.GoldRushEnchantment;
import ru.SocialMoods.SCustomEnchantments.service.EnchantmentService;
import ru.SocialMoods.SCustomEnchantments.service.impl.EnchantmentServiceImpl;

public class Main extends PluginBase {
    @Getter
    private EnchantmentService enchantmentService;
    @Getter
    private static Main instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.enchantmentService = new EnchantmentServiceImpl();

        this.getServer().getPluginManager().registerEvents(new EnchantmentListener(this.enchantmentService), this);
        this.getServer().getCommandMap().register("customenchant", new EnchantCommand());

        EnchantmentAPI.registerEnchantment(new GoldRushEnchantment(), false);
    }
}
