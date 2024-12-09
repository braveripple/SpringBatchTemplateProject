package com.example.springbatch5template.component;

import javax.sql.DataSource;

public class PGCopyBuilder {
	
	private final DataSource dataSource;

	public PGCopyBuilder(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private String format = "TEXT";
	
	private Character delimiter = null;
	
	private Character quote = null;
	
	private Boolean header = null;

	private String encoding = null;
	
	public PGCopyBuilder encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}
	
	public PGCopyBuilder delimiter(char delimiter) {
		this.delimiter = delimiter;
		return this;
	}
	
	public PGCopyBuilder quote(char quote) {
		this.quote = quote;
		return this;
	}
	
	public PGCopyBuilder header(boolean header) {
		this.header = header;
		return this;
	}
	
	public PGCopyUtil build() {
		validate();
		return new PGCopyUtil(dataSource);
	}
	
	private void validate() {
		if ("BINARY".equalsIgnoreCase(format)) {
			// 他オプションの指定がある場合はエラー
			if (delimiter != null || quote != null || header != null || encoding != null) {
				throw new IllegalArgumentException("ERROR");
			}
		} else if ("TEXT".equalsIgnoreCase(format) || "CSV".equalsIgnoreCase(format)) {
			// 他オプションの指定が無い場合デフォルト値を代入
			if (delimiter == null) {
				this.delimiter = '\t';
			}
			if (quote == null) {
				this.quote = '"';
			}
			if (header == null) {
				this.header = false;
			}
			if (header == null) {
				this.header = false;
			}
		}
	}
}
