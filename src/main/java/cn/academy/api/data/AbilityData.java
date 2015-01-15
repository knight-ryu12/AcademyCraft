/**
 * 
 */
package cn.academy.api.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import cn.academy.api.ability.Abilities;
import cn.academy.api.ability.Category;
import cn.academy.api.ability.Level;
import cn.academy.api.ability.SkillBase;
import cn.academy.core.AcademyCraft;
import cn.liutils.util.GenericUtils;
import cn.liutils.util.misc.Pair;

/**
 * @author WeathFolD, acaly
 */
public class AbilityData implements IExtendedEntityProperties {
	
	public static final String IDENTIFIER = "ac_ability";
	
	private final EntityPlayer player;
	
	/*
	 * These fields may be used by Messages.
	 */
	int catID, levelId;
	float currentCP;
	float maxCP;
	float skillExps[];
	int skillLevels[];

	/**
	 * Create an AbilityData for the player with the empty category.
	 */
	public AbilityData(EntityPlayer player) {
		this(player, Abilities.catEmpty);
	}

	/**
	 * Create an AbilityData for the player with the given category.
	 */
	public AbilityData(EntityPlayer player, Category category) {
		this.player = player;
		setInitial(category);
	}
	
	/**
	 * Load the player data from stored NBT.
	 */
	public AbilityData(EntityPlayer player, NBTTagCompound nbt) {
		this(player);
		this.loadNBTData(nbt);
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	//-----Category-----
	public boolean hasAbility() {
		return catID != 0;
	}
	
	public Category getCategory() {
		return Abilities.getCategory(catID);
	}
	
	public int getCategoryID() {
		return catID;
	}
	
	/**
	 * Get the size of skills in player's category.
	 */
	public int getSkillCount() {
		return getCategory().getSkillCount();
	}
	
	public void setCategory(Category cat) {
		if (this.isInSetup) {
			throw new RuntimeException("Cannot modify category during setup api.");
		}
		if (!player.worldObj.isRemote) {
			setInitial(cat);
			//Force reset
			AbilityDataMain.resetPlayer(player);
		}
	}
	
	public void setCategoryID(int value) {
		setCategory(Abilities.getCategory(value));
	}
	
	//-----Level-----
	public Level getLevel() {
		return getCategory().getLevel(levelId);
	}
	
	public int getLevelID() {
		return levelId;
	}
	
	public int getLevelCount() {
		return getCategory().getLevelCount();
	}
	
	public void setLevelID(int value) {
		levelId = value;
		if (isInSetup) {
			getLevel().enterLevel(this);
		} else {
			if (!player.worldObj.isRemote) {
				
				this.isInSetup = true;
				getLevel().enterLevel(this);
				this.isInSetup = false;
				
				syncAll();
			}
		}
	}
	
	//-----Skill-----
	public SkillBase getSkill(int sid) {
		return getCategory().getSkill(sid);
	}
	
	public float getSkillExp(int sid) {
		return skillExps[sid];
	}
	
	public boolean isSkillLearned(int sid) {
		return sid == 0 || skillLevels[sid] > 0;
	}
	
	/**
	 * Return a list containing all skills' ID that the player can learn.
	 */
	public List<Integer> getCanLearnSkillList() {
		return getLevel().getCanLearnSkillList();
	}
	
	/**
	 * Return a list containing all learned skills' ID.
	 */
	public List<Integer> getLearnedSkillList() {
		List<Integer> res = new ArrayList<Integer>();
		for(int i = 0; i < getSkillCount(); ++i) {
			if(isSkillLearned(i)) {
				res.add(i);
			}
		}
		return res;
	}

	/**
	 * Used by Category to initialize AbilityData.
	 * @param values
	 */
	public void setSkillExp(float[] values) {
		this.skillExps = values;
		if (!player.worldObj.isRemote) {
			syncSimple();
		}
	}
	
	public void incrSkillExp(int skillID, float value) {
		setSkillExp(skillID, getSkillExp(skillID) + value);
	}
	
	public int getMaxSkillLevel(int skillID) {
		return getSkill(skillID).getMaxSkillLevel();
	}
	
	public int getSkillLevel(int skillID) {
		return skillLevels[skillID];
	}
	
	public void setSkillLevel(int skillID, int level) {
		skillLevels[skillID] = level;
		if (!player.worldObj.isRemote) {
			syncAll();
		}
	}
	
	/**
	 * Used by Category to initialize AbilityData.
	 * DONT use it in your skill.
	 */
	public void setSkillLevel(int[] values) {
		this.skillLevels = values;
		if (!player.worldObj.isRemote) {
			syncAll();
		}
	}
	
	/**
	 * Only preserved for INTERNAL usage, DONT use it in your skill.
	 */
	@Deprecated
	public float[] getSkillExpArray() {
		return skillExps;
	}
	
	/**
	 * Only preserved for INTERNAL usage, DONT use it in your skill.
	 */
	@Deprecated
	public int[] getSkillLevelArray() {
		return skillLevels;
	}
	
	/**
	 * Use incrSkillExp instead.
	 * @param skillID
	 * @param value
	 */
	@Deprecated
	public void setSkillExp(int skillID, float value) {
		if (isInSetup) {
			float oldValue = skillExps[skillID];
			skillExps[skillID] = value;
			
			getCategory().onSkillExpChanged(this, skillID, oldValue, value);
		} else {
			if (!player.worldObj.isRemote) {
				float oldValue = skillExps[skillID];
				skillExps[skillID] = value;
				
				this.isInSetup = true;
				getCategory().onSkillExpChanged(this, skillID, oldValue, value);
				this.isInSetup = false;
				
				syncSimple();
			}
		}
	}
	
	//-----CP-----
	public float getCurrentCP() {
		return currentCP;
	}
	
	public float getMaxCP() {
		return maxCP;
	}
	
	public void setCurrentCP(float value) {
		currentCP = value;
		
		if (!player.worldObj.isRemote) {
			syncSimple();
		}
	}
	
	public void setMaxCP(float value) {
		maxCP = value;
		currentCP = Math.min(currentCP, maxCP);
		
		if (!player.worldObj.isRemote) {
			syncSimple();
		}
	}
	
	public boolean decreaseCP(float need) {
		if (currentCP < need) return false;
		setCurrentCP(currentCP - need);
		return true;
	}

	private float getRecoverRate() {
		Level lv = GenericUtils.assertObj(getCategory().getLevel(getLevelID()));

		return lv.getInitRecoverCPRate() + 
				(((this.getMaxCP() - lv.getInitialCP()) / (lv.getMaxCP() - lv.getInitialCP())) * 
				(lv.getMaxRecoverCPRate() - lv.getInitRecoverCPRate()));
	}
	
	public boolean recoverCP() {
		if (currentCP < maxCP) {
			float recoverRate = this.getRecoverRate();
			
			float newCP = currentCP + recoverRate;
			newCP = Math.min(newCP, maxCP);
			if (newCP == maxCP || tickCount % 20 == 0) {
				setCurrentCP(newCP);
			} else {
				maxCP = newCP;
			}
		}
		return true;
	}
	
	//-----INTERNAL PROCESSING-----
	@Override
	public void saveNBTData(NBTTagCompound playerNBT) {
		//Create a separated tag node.
		NBTTagCompound nbt = new NBTTagCompound();

		nbt.setInteger("catid", catID);
		nbt.setInteger("levelid", levelId);
		if(getCategory() != null) {
			//Store exps and activate stats
			nbt.setFloat("ccp", currentCP);
			nbt.setFloat("mcp", maxCP);
			int ms = getSkillCount();
			for(int i = 0; i < ms; ++i) {
				nbt.setFloat("sexp_" + i, skillExps[i]);
				nbt.setInteger("slevel_" + i, skillLevels[i]);
			}
		}

		playerNBT.setTag(IDENTIFIER, nbt);
	}

	@Override
	public void loadNBTData(NBTTagCompound playerNBT) {
		int oldCat = catID;
		
		if (!playerNBT.hasKey(IDENTIFIER)) {
			//No data in player's nbt. As this instance is 
			//created as default AbilityData, do nothing.
			return;
		}
		NBTTagCompound nbt = playerNBT.getCompoundTag(IDENTIFIER);
		
		catID = nbt.getInteger("catid");
		levelId = nbt.getInteger("levelid");
		currentCP = nbt.getFloat("ccp");
		maxCP = nbt.getFloat("mcp");
		int ms = getSkillCount();
		skillExps = new float[ms];
		skillLevels = new int[ms];
		for(int i = 0; i < ms; ++i) {
			skillExps[i] = nbt.getFloat("sexp_" + i);
			skillLevels[i] = nbt.getInteger("slevel_" + i);
		}
		
		if (oldCat != catID) {
			Abilities.getCategory(oldCat).onLeaveCategory(this);
			getCategory().onEnterCategory(this);
		}
	}
	
	public void onPlayerTick() {
		if (tickCount == Integer.MAX_VALUE) {
			tickCount = 0;
		} else {
			tickCount++;
		}
		recoverCP();
	}
	
	private void syncSimple() {
		if (!isInSetup) {
			if (needToReset) {
				AbilityDataMain.resetPlayer(player);
				needToReset = false;
			} else {
				AcademyCraft.netHandler.sendTo(new MsgSimpleChange(this), (EntityPlayerMP) player);
			}
		}
	}
	
	private void syncAll() {
		if (!isInSetup) {
			AbilityDataMain.resetPlayer(player);
			needToReset = false;
		} else {
			needToReset = true;
		}
	}
	
	/**
	 * Set the AbilityData to the initial value of the given category.
	 * @param category
	 */
	private void setInitial(Category category) {
		catID = category.getCategoryId();
		
		isInSetup = true;
		category.onInitCategory(this);
		isInSetup = false;
		//Sync is not triggered here.
	}
	
	@Override
	public void init(Entity entity, World world) {}
	
	private boolean isInSetup = false;
	private boolean needToReset = false;
	
	private int tickCount = 0;
}
