package io.fintechlabs.testframework.info;

import java.util.Map;

import io.fintechlabs.testframework.testmodule.TestModule.Status;

public class PublicTestInfo {

	private String _id;
	private String testId;
	private String testName;
	private String variant;
	private String started;
	private String description;
	private String alias;
	private Map<String, String> owner;
	private String planId;
	private Status status;
	private String version;
	private String summary;
	private String publish;
	private String result;

	public String getId() {
		return _id;
	}

	public String getTestId() {
		return testId;
	}

	public String getTestName() {
		return testName;
	}

	public String getVariant() {
		return variant;
	}

	public String getStarted() {
		return started;
	}

	public String getDescription() {
		return description;
	}

	public String getAlias() {
		return alias;
	}

	public Map<String, String> getOwner() {
		return owner;
	}

	public String getPlanId() {
		return planId;
	}

	public Status getStatus() {
		return status;
	}

	public String getVersion() {
		return version;
	}

	public String getSummary() {
		return summary;
	}

	public String getPublish() {
		return publish;
	}

	public String getResult() {
		return result;
	}
}
