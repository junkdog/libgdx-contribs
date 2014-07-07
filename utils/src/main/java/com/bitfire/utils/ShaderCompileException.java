package com.bitfire.utils;

public class ShaderCompileException extends RuntimeException {
	public final String vertexShader;
	public final String fragmentShader;
	public final String log;
	
	public ShaderCompileException( String log, String vertexShader, String fragmentShader ) {
		super();
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		this.log = log;
	}
	
	@Override
	public String getMessage() {
		return String.format("vertex:\n%s\nfragment:\n%s\n%s",
				withLineNumbers(vertexShader), withLineNumbers(fragmentShader), log);
	}
	
	@Override
	public String toString() {
		return ": " + getMessage();
	}
	
	private static String withLineNumbers(String s) {
		StringBuilder sb = new StringBuilder();
		
		String[] lines = s.split("\n");
		for (int i = 0; lines.length > i; i++) {
			sb.append(String.format("%03d: %s\n", (i + 1), lines[i]));
		}
		
		return sb.toString();
	}
}
