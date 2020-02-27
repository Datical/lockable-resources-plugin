/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2016, Florian Hug. All rights reserved.             *
 *                                                                     *
 * This file is part of the Jenkins Lockable Resources Plugin and is   *
 * published under the MIT license.                                    *
 *                                                                     *
 * See the "LICENSE.txt" file for more information.                    *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package org.jenkins.plugins.lockableresources.queue;

import java.io.Serializable;
import java.util.List;

import hudson.model.Run;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.kohsuke.stapler.export.Exported;

/*
 * This class is used to queue pipeline contexts
 * which shall be executed once the necessary
 * resources are free'd.
 */
public class QueuedContextStruct implements Serializable {

	/*
	 * Reference to the pipeline step context.
	 */
	private StepContext context;

	/*
	 * Reference to the resources required by the step context.
	 */
	private List<LockableResourcesStruct> lockableResourcesStruct;

	/*
	 * Description of the required resources used within logging messages.
	 */
	private String resourceDescription;

	// build information in the requesting context. Useful for displaying on the ui and logging
	private transient volatile Run<?, ?> build = null;
	private transient volatile String buildExternalizableId = null;

	/*
	 * Name of the variable to save the locks taken.
	 */
	private String variableName;

	/*
	 * Constructor for the QueuedContextStruct class.
	 */
	public QueuedContextStruct(StepContext context, List<LockableResourcesStruct> lockableResourcesStruct, String resourceDescription, String variableName) {
		this.context = context;
		this.lockableResourcesStruct = lockableResourcesStruct;
		this.resourceDescription = resourceDescription;
		this.variableName = variableName;
	}

	/*
	 * Gets the pipeline step context.
	 */
	public StepContext getContext() {
		return this.context;
	}

	@Exported
	public String getBuildExternalizableId() {
		if (this.buildExternalizableId == null) {
			// getting the externalizableId can fail for many reasons, set to null if it fails for some reason
			try {
				buildExternalizableId = this.context.get(Run.class).getExternalizableId();
			} catch (Exception e) {
				buildExternalizableId = null;
			}
		}
		return this.buildExternalizableId;
	}

	public Run<?, ?> getBuild() {
		if (build == null) {
			build = Run.fromExternalizableId(getBuildExternalizableId());
		}
		return build;
	}

	@Exported
	public String getBuildName() {
		if (getBuild() != null)
			return getBuild().getFullDisplayName();
		else
			return null;
	}

	/*
	 * Gets the required resources.
	 */
	public List<LockableResourcesStruct> getResources() {
		return this.lockableResourcesStruct;
	}

	/*
	 * Gets the resource description for logging messages.
	 */
	@Exported
	public String getResourceDescription() {
		return this.resourceDescription;
	}

	/*
	 * Gets the variable name to save the locks taken.
	 */
	public String getVariableName() {
		return this.variableName;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "Build(" + getBuildExternalizableId() + ") Resource(" + resourceDescription + ")";
	}
}
