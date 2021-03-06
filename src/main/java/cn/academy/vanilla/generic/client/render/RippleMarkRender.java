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
package cn.academy.vanilla.generic.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cn.academy.core.client.Resources;
import cn.academy.vanilla.generic.entity.EntityRippleMark;
import cn.liutils.render.material.SimpleMaterial;
import cn.liutils.render.mesh.Mesh;
import cn.liutils.util.generic.MathUtils;
import cn.liutils.util.helper.GameTimer;

/**
 * @author WeAthFolD
 */
public class RippleMarkRender extends Render {
	
	final long CYCLE = 3600;
	final long timeOffsets[] = { 0, -1200, -2400 };
	Mesh mesh;
	SimpleMaterial material;
	
	public RippleMarkRender() {
		mesh = new Mesh();
		mesh.setVertices(new double[][] {
				{ -.5, 0, -.5 },
				{ .5,  0, -.5 },
				{ .5,  0, .5  },
				{ -.5, 0, .5  }
		});
		mesh.setUVs(new double[][] {
				{0, 0},
				{0, 1},
				{1, 1},
				{1, 0}
		});
		mesh.setQuads(new int[] { 0, 1, 2, 3 });
		material = new SimpleMaterial(Resources.getTexture("effects/ripple"));
		material.ignoreLight = true;
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float a, float b) {
		EntityRippleMark mark = (EntityRippleMark) entity;
		long dt = GameTimer.getTime() - mark.creationTime;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		GL11.glDepthMask(false);
		GL11.glPushMatrix();
		
		GL11.glTranslated(x, y, z);
		
		for(int i = 0; i < timeOffsets.length; ++i) {
			GL11.glPushMatrix();
			
			long mod = (dt - timeOffsets[i]) % CYCLE;
			float size = getSize(mod);
			
			GL11.glTranslatef(0, getHeight(mod), 0);
			GL11.glScalef(size, 1, size);
			material.color = mark.color.copy();
			material.color.a *= getAlpha(mod);
			mesh.draw(material);
			
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1f);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	private float getHeight(long mod) {
		return mod * 3e-4f;
	}
	
	private float getAlpha(long mod) {
		final float BIN = 1600, BOUT = 1600;
		if(mod < BIN)
			return mod / BIN;
		if(mod > CYCLE - BOUT)
			return 1 - (mod - (CYCLE - BOUT)) / BOUT;
		return 1.0f;
	}
	
	private float getSize(long mod) {
		return MathUtils.lerpf(1.9f, 1.4f, (float) mod / CYCLE);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

}
