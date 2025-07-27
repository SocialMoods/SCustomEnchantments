package ru.SocialMoods.SCustomEnchantments.utils;

import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.utils.DynamicClassLoader;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

/**
 * @deprecated это не самый лучший способ, да и нет смысла в нем
 */
@Deprecated
@UtilityClass
public class BookUtils {

    @SuppressWarnings("unchecked")
    public static void registerEnchantedBooks(CustomEnchantment enchantment) {
        Objects.requireNonNull(enchantment, "Enchantment cannot be null");

        int maxLevel = enchantment.getMaxLevel();
        if (maxLevel < 1) {
            return;
        }

        for (int level = 1; level <= maxLevel; level++) {
            String bookName = String.format("§fЗачарованная книга\n§7%s %s",
                    enchantment.getDisplayName(),
                    NBTUtils.toRoman(level));

            try {
                String className = "CustomEnchantedBook_" + enchantment.getId() + "_" + level;
                ClassWriter cw = new ClassWriter(0);

                cw.visit(V17, ACC_PUBLIC | ACC_SUPER,
                        "cn/nukkit/item/customitem/" + className,
                        null,
                        "cn/nukkit/item/customitem/ItemCustomBookEnchanted",
                        null);

                MethodVisitor mv = cw.visitMethod(
                        ACC_PUBLIC,
                        "<init>",
                        "()V",
                        null,
                        null);

                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitLdcInsn("scustom:" + enchantment.getId() + "_" + level);
                mv.visitLdcInsn(bookName);
                mv.visitMethodInsn(
                        INVOKESPECIAL,
                        "cn/nukkit/item/customitem/ItemCustomBookEnchanted",
                        "<init>",
                        "(Ljava/lang/String;Ljava/lang/String;)V",
                        false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 1);
                mv.visitEnd();

                cw.visitEnd();

                Class<? extends CustomItem> bookClass = (Class<? extends CustomItem>)
                        new DynamicClassLoader()
                                .defineClass("cn.nukkit.item.customitem." + className, cw.toByteArray());

                Item.registerCustomItem(bookClass).assertOK();

            } catch (Exception e) {
                return;
            }
        }

    }
}
