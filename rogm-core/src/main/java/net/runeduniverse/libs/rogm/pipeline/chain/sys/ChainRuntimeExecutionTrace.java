package net.runeduniverse.libs.rogm.pipeline.chain.sys;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ChainRuntimeExecutionTrace {

	public static CharSequence OFFSET = "\t";
	public static CharSequence ELEMENT_PREFIX = " ├ ";
	public static CharSequence LAST_ELEMENT_PREFIX = " └ ";

	public static String HEAD_TXT = "CHAIN EXECUTION TRACE >> %s%s";
	public static String METHOD_ENTRY_TXT = "[%i] ENTRY >> %s.%s";
	public static String JUMP_TXT = "[%i] JUMP  >> [%i]";
	public static String REPORT_TXT = "[%i] CHAIN >> %s%s";

	private final ChainRuntimeExecutionTrace root;
	@Getter
	private final int traceLevel;
	private final String chainLabel;
	private boolean dirty = true;
	private String report;

	private int activeExecutionLevel = -1;
	private List<String> entries = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public ChainRuntimeExecutionTrace(ChainRuntime<?> runtime, String chainLabel) {
		if (runtime == null) {
			this.root = null;
			this.traceLevel = 1;
		} else {
			this.root = runtime.getTrace();
			this.traceLevel = this.root.getTraceLevel() + 1;
		}
		this.chainLabel = chainLabel;
	}

	public void setCurrentLayer(int i) {
		this.dirty = true;
		this.activeExecutionLevel = i;
	}

	public void methodEntry(String className, String methodName) {
		this.dirty = true;
		this.entries.add(String.format(METHOD_ENTRY_TXT, this.activeExecutionLevel, className, methodName));
	}

	public void jumpToLayer(int jumpLayer) {
		this.dirty = true;
		this.entries.add(String.format(JUMP_TXT, this.activeExecutionLevel, jumpLayer));
	}

	private void appendReport(String chainName, String report) {
		this.dirty = true;
		this.entries.add(String.format(REPORT_TXT, this.activeExecutionLevel, report));
	}

	private String buildReport() {
		if (this.dirty == false)
			return this.report;
		String effectiveOffset = "\n";
		for (int i = 0; i < this.traceLevel; i++)
			effectiveOffset = effectiveOffset + OFFSET;
		this.report = effectiveOffset + String.join(effectiveOffset, this.entries);
		this.dirty = false;
		return this.report;
	}

	@Override
	public String toString() {
		return String.format(HEAD_TXT, this.chainLabel, this.buildReport());
	}

	public String report(ChainLogger logger) {
		if (this.root == null)
			logger.logTrace(this);
		else
			this.root.appendReport(this.chainLabel, this.buildReport());
		return this.report;
	}
}
