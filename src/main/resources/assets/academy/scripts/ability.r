ac {
    ability {
        learning {
            # 学习一个技能的消耗。 返回值：刺激数（被约到整数），参数：技能等级
            learning_cost(skillLevel) {
                floor(3 + skillLevel * skillLevel * 0.5)
            }
			# 升级消耗。返回值：刺激数，参数：到达等级
			uplevel_cost(level) {
				5 * level
			}
        }
        
        cp {
            recover_cooldown { 15 } # 技能使用后，CP恢复开始的冷却时间。
            recover_speed(cp, maxcp) { # CP的恢复速度。参数：当前CP，最大CP
                0.0001 * maxcp * lerp(1, 2, cp / maxcp)
            }
            
            overload_cooldown { 32 } # 技能使用后，过载恢复开始的冷却时间。
            overload_recover_speed(o, maxo) { # 过载的恢复速度。 参数：当前过载，最大过载
                max(0.002 * maxo, 
                    0.007 * maxo * lerp(1, 0.5, o / (maxo * 2)))
            }
            
            # 在过载时CP和过载消耗（增量）所乘的倍数。
            overload_cp_mul { 2.5 }
            overload_o_mul { 1.5 }
			
			# CP消耗和最大值CP增加的转换
			maxcp_rate(cp) { cp / 40.0 }
			
			# 过载和最大过载值增加的转换
			maxo_rate(o) { o / 170.0 }
            
            # 各个等级初始CP值
            init_cp(level) {
                switch(level) {
					0: 1800;
                    1: 1800;
                    2: 2800;
                    3: 4000;
                    4: 5800;
                    5: 8000
                }
            }
			
			# 各个等级最大提高的CP值
			add_cp(level) {
				switch(level) {
					0: 0;
					1: 900;
					2: 1000;
					3: 1500;
					4: 1700;
					5: 12000
				}
			}
            
            # 各个等级初始过载值
            init_overload(level) {
                switch(level) {
					0: 100;
                    1: 100;
                    2: 150;
                    3: 240;
                    4: 350;
                    5: 500
                }
            }
			
			# 各个等级最大提高的过载值
			add_overload(level) {
				switch(level) {
					0: 0;
					1: 40;
					2: 70;
					3: 80;
					4: 100;
					5: 500
				}
			}
        }
    }
}