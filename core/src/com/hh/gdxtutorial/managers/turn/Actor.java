package com.hh.gdxtutorial.managers.turn;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Actor {
	public static final int PLAYER = 0;
	public static final int MOB = 1;

	public ModelInstance instance;
	public int type;
	public float zPos = 2.0f;
	public Vector3 position = new Vector3();
	public Vector3 start = new Vector3();
	public Vector3 end = new Vector3();
	public boolean inTurn = false;
	public Vector3 direction;

	public Actor() {}
	public Actor(ModelInstance instance, int type) {
		this.instance = instance;
		this.type = type;
	}

	public void act() {
//		if (inTurn) update();
//		else startTurn();
	}
	public void startTurn() {
		inTurn = true;
		// cache the current translation as start
//		instance.transform.getTranslation(start);
		start.set(position);
		// get random new values for x and z
		float x = MathUtils.random(-20, 20);
		float z = MathUtils.random(-20, 20);
		// cache the generated destination as end
		end.set(x, 2, z);
		// get the normalized direction vector to translate with
		direction = start.sub(end).nor();
	}
	public void update(float delta) {
		if (inTurn) {
			// get the current position
//			instance.transform.getTranslation(position);

			if (start.dst(position) <= start.dst(end)) {
				position.add(-direction.x * delta * 10, 0, -direction.z * delta * 10);
//				instance.transform.translate(-direction.x * delta * 10, 0, -direction.z * delta * 10);
			} else {
				System.out.println("ending");

				position.set(end);
//				instance.transform.setTranslation(end);
				endTurn();
			}
			// cache the translation as position
//			instance.transform.getTranslation(position);
//			instance.transform.setTranslation(position);
		}

		instance.transform.setTranslation(position);
	}

	public void endTurn() {
		inTurn = false;
	}
}
