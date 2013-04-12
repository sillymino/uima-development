package org.barcelonamedia.uima;

public class CotigToken {
	public int begin, end;
	public String type, source, normalized, shape;
	public CotigToken(int begin, int end, String type, String source,
			String normalized, String shape) {
		super();
		this.begin = begin;
		this.end = end;
		this.type = type;
		this.source = source;
		this.normalized = normalized;
		this.shape = shape;
	}
	public CotigToken(int begin, int end, String type) {
		super();
		this.begin = begin;
		this.end = end;
		this.type = type;
	}
	
}
