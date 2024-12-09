package com.example.springbatch5template.component;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

@Component
public class PGCopyUtil {
	
	private final DataSource dataSource;

	private String optionFormat = "TEXT";
	
	private boolean optionHeader = false;
	
	private char optionDelimiter = '\t';
	
	private char optionQuote = '"';
	
	public PGCopyUtil(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public PGCopyUtilCore tablename(String tableOrQuery) {
		return new PGCopyUtilCore(tableOrQuery);
	}
	
	class PGCopyUtilCore {

		// 必須
		private final String tablename;

		// 任意
		private Path filePath = null;

		private String format = null;
		
		private Boolean header = null;

		private Character delimiter = null;
		
		private Character quote = null;
		
		public PGCopyUtilCore(String tablename) {
			this.tablename = tablename;
		}
		
		public PGCopyUtilCore format(String format) {
			if (!Set.of("TEXT", "CSV", "BINARY").contains(format)) {
				throw new IllegalArgumentException("unrecognized format : " + format);
			}
			this.format = format;
			return this;
		}
		
		public PGCopyUtilCore delimiter(char delimiter) {
			this.delimiter = delimiter;
			return this;
		}
		
		public PGCopyUtilCore quote(char quote) {
			this.quote = quote;
			return this;
		}
		
		public PGCopyUtilCore header(boolean header) {
			this.header = header;
			return this;
		}
		
		public long copyOut() {
			setOption();
			final String sql = "";
			return 0;
		}

		public long copyIn() {
			setOption();
			final String sql = "";
			return 0;
		}
		
		private void setOption() {
			this.format = Objects.requireNonNullElse(this.format, optionFormat);
			this.header = Objects.requireNonNullElse(this.header, optionHeader);
			this.delimiter = Objects.requireNonNullElse(this.delimiter, optionDelimiter);
			this.quote = Objects.requireNonNullElse(this.quote, optionQuote);
		}
	}

	public void setFormat(String format) {
		if (!Set.of("TEXT", "CSV", "BINARY").contains(format)) {
			throw new IllegalArgumentException("unrecognized format : " + format);
		}
		this.optionFormat = format;
	}
	
	public void setHeader(boolean header) {
		this.optionHeader = header;
	}
	
	public void setDelimiter(char delimiter) {
		this.optionDelimiter = delimiter;
	}
	
	public void setQuote(char quote) {
		this.optionQuote = quote;
	}
}
