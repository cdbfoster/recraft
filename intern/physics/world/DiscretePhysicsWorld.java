/********************************************************************************
 *                                                                              *
 *  This file is part of the Tiny Physics Library.                              *
 *                                                                              *
 *  Copyright 2012 Chris Foster                                                 *
 *                                                                              *
 ********************************************************************************/

package physics.world;

import java.util.LinkedList;
import java.util.List;

import physics.core.Broadphase;
import physics.core.Broadphase.BroadphasePair;
import physics.core.Narrowphase;
import physics.core.PhysicsObject;
import physics.core.PhysicsWorld;
import collision.core.CollisionResult;

public class DiscretePhysicsWorld implements PhysicsWorld
{
	public static final int maxSubSteps = 10;
	public static final float subStepInterval = 0.05f;

	protected Broadphase broadphase;
	protected Narrowphase narrowphase;

	protected List<PhysicsObject> worldObjects;
	protected float internalTime;

	public DiscretePhysicsWorld(Broadphase broadphase, Narrowphase narrowphase)
	{
		this.broadphase = broadphase;
		this.narrowphase = narrowphase;

		this.worldObjects = new LinkedList<PhysicsObject>();
		this.internalTime = 0.0f;
	}

	@Override
	public void addPhysicsObject(PhysicsObject object)
	{
		this.worldObjects.add(object);
	}

	@Override
	public void removePhysicsObject(PhysicsObject object)
	{
		this.worldObjects.remove(object);
	}

	@Override
	public void clearWorld()
	{
		this.worldObjects.clear();
	}

	@Override
	public void stepWorld(float timeStep)
	{
		int simulationSubSteps = 0;

		this.internalTime += timeStep;
		if (this.internalTime > subStepInterval)
		{
			simulationSubSteps = (int)Math.floor(this.internalTime / subStepInterval);
			this.internalTime -= simulationSubSteps * subStepInterval;
		}

		if (simulationSubSteps > 0)
		{
			simulationSubSteps = (simulationSubSteps > maxSubSteps) ? maxSubSteps : simulationSubSteps;

			this.applyWorldForces();

			for (int i = 0; i < simulationSubSteps; i++)
			{
				this.stepSimulation(subStepInterval);
			}
		}
		else
		{
			this.integrateVelocities(this.internalTime);
		}
	}

	protected void applyWorldForces()
	{
		// TODO Apply DiscretePhysicsWorld forces
	}

	protected void stepSimulation(float timeStep)
	{
		this.predictMotion(timeStep);

		List<CollisionResult> collisions = this.detectCollision();

		this.solveConstraints(collisions);
	}

	protected void predictMotion(float timeStep)
	{
		this.integrateVelocities(timeStep);
	}

	protected void integrateVelocities(float timeStep)
	{
		// TODO Integrate velocities
	}

	protected List<CollisionResult> detectCollision()
	{
		List<BroadphasePair> broadphasePairs = this.broadphase.calculateOverlappingPairs(worldObjects);

		List<CollisionResult> collisions = this.narrowphase.calculateCollisions(broadphasePairs);

		return collisions;
	}

	protected void solveConstraints(List<CollisionResult> collisions)
	{
		// TODO Solve constraints
	}
}
