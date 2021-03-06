package cn.academy.support.rf;

import cn.academy.core.AcademyCraft;
import cn.academy.crafting.ModuleCrafting;
import cn.academy.energy.ModuleEnergy;
import cn.academy.support.BlockConverterBase;
import cn.academy.support.EnergyBlockHelper;
import cn.annoreg.core.RegWithName;
import cn.annoreg.core.Registrant;
import cn.annoreg.mc.RegBlock;
import cn.annoreg.mc.RegInit;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

@Registrant
@RegInit
public class RFSupport {
	
	/** The convert rate (1IF = <CONV_RATE> RF) */
	public static final double CONV_RATE = 4;
	
	@RegBlock(item = BlockConverterBase.Item.class)
	@RegWithName("rf_input")
	public static Block rfInput = new BlockRFInput();
	
	@RegBlock(item = BlockConverterBase.Item.class)
	@RegWithName("rf_output")
	public static Block rfOutput = new BlockRFOutput();
	
	// Convert macros, dividing by hand is error-prone
	/**
	 * Converts RF to equivalent amount of IF.
	 */
	public static double rf2if(int rfEnergy) {
		return rfEnergy / CONV_RATE;
	}
	/**
	 * Converts IF to equivalent amount of RF.
	 */
	public static int if2rf(double ifEnergy) {
		return (int) (ifEnergy * CONV_RATE);
	}
	
	public static void init() {
		
		try {
			// ACTutorial.addTutorial("energy_bridge_rf").addCondition(Condition.or(Condition.itemsCrafted(rfInput,rfOutput)));
		} catch (Exception e) {
			AcademyCraft.log.error(e);
		}
		
		EnergyBlockHelper.register(new RFProviderManager());
		EnergyBlockHelper.register(new RFReceiverManager());
		
		GameRegistry.addRecipe(new ItemStack(rfInput), "abc", " d ",
				'a', ModuleEnergy.energyUnit, 'b', ModuleCrafting.machineFrame,
				'c', ModuleCrafting.constPlate, 'd', ModuleCrafting.convComp);
		
		GameRegistry.addRecipe(new ItemStack(rfOutput), "abc", " d ",
				'a', ModuleEnergy.energyUnit, 'b', ModuleCrafting.machineFrame,
				'c', ModuleCrafting.resoCrystal, 'd', ModuleCrafting.convComp);
		
		GameRegistry.addRecipe(new ItemStack(rfInput), "X",'X',new ItemStack(rfOutput));
		GameRegistry.addRecipe(new ItemStack(rfOutput), "X",'X',new ItemStack(rfInput));
	}
	
}
