package cn.academy.core.entity;

import net.minecraft.util.Vec3;
import cn.liutils.util.client.ViewOptimize.IAssociatePlayer;

/**
 * States used in RenderRay classes.
 * The view direction of the ray is determined by the rotationYaw and rotationPitch.
 * @author WeAthFolD
 */
public interface IRay extends IAssociatePlayer {
	
	void onRenderTick();
	
	Vec3 getPosition();
	
	/**
	 * @return If this ray is spawned at player's hand and need to be treated differently for 1st and 3rd person
	 */
	boolean needsViewOptimize();
	
	double getLength();
	
	//---Advanced parameters
	/**
	 * @return An alpha multiplyer. Can be used for blend out.
	 */
	double getAlpha();
	
	/**
	 * @return The alpha multiplyer of the glow texture.
	 */
	double getGlowAlpha();
	
	/**
	 * Get the advance distance of the ray starting point. Can be used for blend out.
	 */
	double getStartFix();
	
	/**
	 * Get the current ray width multiplyer. Used for blending
	 */
	double getWidth();
	
}
