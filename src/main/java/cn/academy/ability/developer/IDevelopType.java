/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * This project is open-source, and it is distributed under  
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * 本项目是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.academy.ability.developer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * An IDevelopType represents a single kind of process to be performed in Developer.
 * You have to provide the stimulation count and the learned callback.
 * @author WeAthFolD
 */
public interface IDevelopType {
	
	int getStimulations(EntityPlayer player);
	
	/**
	 * @param player Target player
	 * @param developer The developer that is performing this action
	 * @return Whether the action can be REALLY started/performed at the moment.
	 * This should be some constraints to player's current state.
	 */
	boolean validate(EntityPlayer player, Developer developer);
	
	/**
	 * The action performed when really learned the develop type.
	 */
	void onLearned(EntityPlayer player);
	
	/**
	 * @return The icon displayed in develop progress screen
	 */
	ResourceLocation getIcon(EntityPlayer player);
	
	/**=
	 * @return The name displayed in develop progress screen
	 */
	String getName(EntityPlayer player);
	
}
