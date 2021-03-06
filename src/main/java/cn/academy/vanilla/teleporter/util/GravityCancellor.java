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
package cn.academy.vanilla.teleporter.util;

import net.minecraft.entity.player.EntityPlayer;
import cn.liutils.core.event.eventhandler.LIHandler;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

/**
 * @author WeAthFolD
 */
public class GravityCancellor extends LIHandler<ClientTickEvent> {
	
	final EntityPlayer p;
	final int ticks;
	int ticker = 0;
	
	public GravityCancellor(EntityPlayer _p, int _ticks) {
		p = _p;
		ticks = _ticks;
	}

	@Override
	protected boolean onEvent(ClientTickEvent event) {
		if(p.isDead || (++ticker == ticks)) {
			this.setDead();
		} else {
			if(!p.capabilities.isFlying) {
				if(!p.onGround) {
					p.motionY += 0.036;
				}
			}
		}
		return true;
	}

}
