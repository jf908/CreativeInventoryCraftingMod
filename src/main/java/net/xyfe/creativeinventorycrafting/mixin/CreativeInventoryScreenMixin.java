package net.xyfe.creativeinventorycrafting.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.xyfe.creativeinventorycrafting.CreativeInventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
	@Shadow private static int selectedTab;

	@Unique
	private static final Identifier GUI_TEXTURE = new Identifier(CreativeInventoryCrafting.MODID, "textures/gui/tab_inventory.png");

	@Unique
	private int currentSlot = -1;

	@Unique
	private final static int armorOffset = -27;

	public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Redirect(method = "drawBackground", at = @At(value = "NEW", target = "net/minecraft/util/Identifier"))
	private Identifier getTexture(String s) {
		if(ItemGroup.GROUPS[selectedTab].equals(ItemGroup.INVENTORY)) {
			return GUI_TEXTURE;
		} else {
			return new Identifier(s);
		}
	}

	@Inject(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), locals =  LocalCapture.CAPTURE_FAILSOFT)
	private void saveLocals(ItemGroup group, CallbackInfo info, int i, ScreenHandler screenHandler, int l, int y, int aa) {
		currentSlot = l;
	}

	@ModifyVariable(method = "setSelectedTab", index = 5, at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
	private int setCraftingSlotX(int t) {
		if(currentSlot == 0) {
			// Move crafting result
			t = 173;
		} else if(currentSlot >= 1 && currentSlot < 5) {
			// Move crafting grid
			t = 117 + ((currentSlot-1) % 2)*18;
		} else if((currentSlot >= 5 && currentSlot < 9) || currentSlot == 45) {
			// Move armour and offhand slots
			t += armorOffset;
		}
		return t;
	}

	@ModifyVariable(method = "setSelectedTab", index = 6, at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
	private int setCraftingSlotY(int aa) {
		if(currentSlot == 0) {
			// Move crafting result
			aa = 21;
		} else if(currentSlot >= 1 && currentSlot < 5) {
			// Move crafting grid
			aa = 11 + ((currentSlot-1) / 2)*18;
		}
		return aa;
	}

    // Change InventoryScreen entity render location
	@ModifyConstant(method = "drawBackground", constant = @Constant(intValue = 88))
	private int inventoryEntityX(int x) {
		return x + armorOffset;
	}
}