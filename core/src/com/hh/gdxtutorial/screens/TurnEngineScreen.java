package com.hh.gdxtutorial.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.hh.gdxtutorial.shaders.GaussianBlurShaderProgram;

/**
 * Created by nils on 5/27/16.
 */
public class TurnEngineScreen  extends AbstractScreen {
	public PerspectiveCamera camera;
	public CameraInputController camController;

	public AssetManager assetManager;
	public Array<ModelInstance> instances = new Array<ModelInstance>();

	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;

	public GaussianBlurShaderProgram gaussianBlurShader;

	public Array<FrameBuffer> fbos = new Array<FrameBuffer>();
	public TextureRegion tr = new TextureRegion();
	public Environment environment;

	@Override
	public void show() {
		fbos.setSize(2);

		// declare and configure the camera.
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0.48f, 5.67f, 2.37f);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 1000;
		camera.update();
		// declare camController and set it as the input processor.
		camController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(camController);

		gaussianBlurShader = new GaussianBlurShaderProgram();
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();
		spriteBatch.setShader(gaussianBlurShader);


		environment = new Environment();
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, 0.5f, -1.0f));

		assetManager = new AssetManager();
		assetManager.load("models/plane.g3dj", Model.class);
	}

	@Override
	public void doneLoading() {
		super.doneLoading();

		ModelInstance instance = new ModelInstance(assetManager.get("models/plane.g3dj", Model.class));
		instances.add(instance);
	}
}
