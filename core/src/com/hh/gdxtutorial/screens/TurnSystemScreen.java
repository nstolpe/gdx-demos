package com.hh.gdxtutorial.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.hh.gdxtutorial.engines.turn.Actor;
import com.hh.gdxtutorial.engines.turn.TurnEngine;
import com.hh.gdxtutorial.entity.components.*;
import com.hh.gdxtutorial.entity.systems.ModelBatchPass;
import com.hh.gdxtutorial.entity.systems.TurnSystem;
import com.hh.gdxtutorial.screens.input.TurnInputController;

/**
 * Created by nils on 5/27/16.
 */
public class TurnSystemScreen extends FpsScreen {
	public Engine engine = new Engine();
	public PerspectiveCamera camera;
	public TurnInputController camController;

	public AssetManager assetManager;
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public Array<Actor> actors = new Array<Actor>();
	public ModelInstance plane;

	public ModelBatch modelBatch;

	public Environment environment;
	public Texture tex;

	protected Label turnLabel;

//	public TurnEngine turnEngine = new TurnEngine();

	@Override
	public void show() {
		super.show();
		turnLabel = new Label(" ", new Label.LabelStyle(font, Color.WHITE));
		table.row();
		table.add(turnLabel).expandY().bottom();
		// declare and configure the camera.
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(20.0f, 20.0f, 20.0f);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 1000;
		camera.update();
		// declare camController and set it as the input processor.
		camController = new TurnInputController(camera);
		multiplexer.addProcessor(camController);

		modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, 0.5f, -1.0f));

		assetManager = new AssetManager();
		assetManager.load("models/plane.g3dj", Model.class);
		assetManager.load("models/sphere.g3dj", Model.class);
	}
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camController.update();
		MessageManager.getInstance().update();

		if (loading && assetManager.update()) doneLoading();

		stringBuilder.setLength(0);
		stringBuilder.append(" Turn: ").append(engine.getSystem(TurnSystem.class) == null ? "" : engine.getSystem(TurnSystem.class).turnCount + ": " + engine.getSystem(TurnSystem.class).activeIndex());
		turnLabel.setText(stringBuilder);

		engine.update(delta);
		super.render(delta);
	}
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}
	@Override
	public void doneLoading() {
		super.doneLoading();
		setupScene();
		setupActors();
		engine.addSystem(new TurnSystem());
		engine.addSystem(new ModelBatchPass(modelBatch, camera, environment));
	}

	public void setupActors() {
		Entity player = new Entity()
			.add(new PositionComponent(new Vector3(0, 2, 0)))
			.add(new ModelInstanceComponent(new ModelInstance(assetManager.get("models/sphere.g3dj", Model.class))))
			.add(new InitiativeComponent(MathUtils.random(10)))
			.add(new PlayerComponent());

		engine.addEntity(player);

		// create texture for mobs
		tex = new Texture(Gdx.files.internal("models/sphere-purple.png"), true);
		tex.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
		TextureAttribute texAttr = new TextureAttribute(TextureAttribute.Diffuse, tex);

		// create and position the mobs spheres/Actors
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				ModelInstance mi = new ModelInstance(assetManager.get("models/sphere.g3dj", Model.class));
				mi.getMaterial("skin").set(texAttr);
				mi.getMaterial("skin").set(new ColorAttribute(ColorAttribute.Diffuse, MathUtils.random(), MathUtils.random(), MathUtils.random(), 1.0f));
				Entity mob = new Entity()
						.add(new PositionComponent(new Vector3(i * 20, 2, j * 20)))
						.add(new ModelInstanceComponent(mi))
						.add(new InitiativeComponent(MathUtils.random(10)))
						.add(new AiComponent());
				engine.addEntity(mob);
			}
		}
	}

	public void setupScene() {
		Entity p = new Entity()
				.add(new ModelInstanceComponent( new ModelInstance(assetManager.get("models/plane.g3dj", Model.class))))
				.add(new PositionComponent(new Vector3(0.0f, 0.0f, 0.0f)));

		engine.addEntity(p);
	}

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		modelBatch.dispose();
		instances.clear();
		tex.dispose();
	}
}