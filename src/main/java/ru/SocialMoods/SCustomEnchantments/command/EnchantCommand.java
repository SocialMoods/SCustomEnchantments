package ru.SocialMoods.SCustomEnchantments.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import ru.SocialMoods.SCustomEnchantments.api.EnchantmentAPI;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;
import ru.SocialMoods.SCustomEnchantments.utils.NBTUtils;

public class EnchantCommand extends Command {

    public EnchantCommand() {
        super("customenchant", "Custom enchantments");

        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("id", CommandParamType.STRING),
                CommandParameter.newType("level", CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only for player!");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /customenchant <id> <level>");
            return false;
        }

        Item item = player.getInventory().getItemInHand();

        if (item.isNull()) {
            sender.sendMessage("You need to held item!");
            return false;
        }

        String enchantmentId = args[0];
        int level;

        try {
            level = Integer.parseInt(args[1]);
            if (level <= 0) {
                sender.sendMessage("Level must be positive!");
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Wrong number format");
            return false;
        }

        CustomEnchantment enchantment = EnchantmentAPI.getEnchantment(enchantmentId);
        if (enchantment == null) {
            sender.sendMessage("Enchantment with id " + enchantmentId + " not found! Please, register it with API.");
            sender.sendMessage("All registered enchantments: " +
                    String.join(", ", EnchantmentAPI.getAllEnchantments()
                            .stream()
                            .map(CustomEnchantment::getId)
                            .toArray(String[]::new)));
            return false;
        }

        if (!enchantment.isSupported(item)) {
            sender.sendMessage("Enchantment dont supported on this item");
            return false;
        }

        NBTUtils.addCustomEnchantment(item, enchantmentId, enchantment.getDisplayName(), level);
        player.getInventory().setItemInHand(item);

        sender.sendMessage("Successfully enchanted " +
                enchantment.getDisplayName() + " " + level + " on item");

        return true;
    }
}