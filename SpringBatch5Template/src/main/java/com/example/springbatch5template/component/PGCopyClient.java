package com.example.springbatch5template.component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.postgresql.PGConnection;
import org.slf4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

@Component
public class PGCopyClient {
	
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PGCopyClient.class);
	
	private final DataSource dataSource;

	private String optionFormat = "TEXT";
	
	private boolean optionHeader = false;
	
	private char optionDelimiter = '\t';
	
	private char optionQuote = '"';
	
	public PGCopyClient(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setFormat(String format) {
		if (!Set.of("TEXT", "CSV", "BINARY").contains(format.toUpperCase())) {
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
	
	public PGCopyUtilCore tablename(String tableOrQuery) {
		return new PGCopyUtilCore(tableOrQuery);
	}
	
	public class PGCopyUtilCore {

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
		
		public PGCopyUtilCore file(Path filePath) {
			this.filePath = filePath;
			return this;
		}
		
		public PGCopyUtilCore format(String format) {
			if (!Set.of("TEXT", "CSV", "BINARY").contains(format.toUpperCase())) {
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
		
		public long copyOut() throws IOException, SQLException {
			setOption();
			
			final StringBuilder sb = new StringBuilder();
			sb.append(String.format("COPY %s TO STDOUT WITH (", tablename));
			sb.append(String.format("  FORMAT %s", format));
			if ("TEXT".equalsIgnoreCase(format) || "CSV".equalsIgnoreCase(format)) {
				sb.append(String.format(", ENCODING '%s'", "UTF-8"));
				sb.append(String.format(", HEADER %b", header));
				sb.append(String.format(", DELIMITER E'%c'", delimiter));
				if ("CSV".equalsIgnoreCase(format)) {
					sb.append(String.format(", QUOTE E'%c'", quote));
				}
			}
			sb.append(");");
			
			final String sql = sb.toString();

			logger.debug(sql);
			
			// filePath指定が無い場合は作成する。
			if (filePath == null) {
				if (isTableName(tablename)) {
					filePath = Path.of(tablename + ".dat");
				} else {
					filePath = Path.of("query.dat");
				}
			}

			Connection connection = null;
				
			try {
				connection = DataSourceUtils.getConnection(dataSource);
				try (final BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
					return connection.unwrap(PGConnection.class)
						.getCopyAPI()
						.copyOut(sql, writer);
				}
			} finally {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}
		}

		public long copyIn() throws IOException, SQLException {
			
			setOption();
			
			final StringBuilder sb = new StringBuilder();
			sb.append(String.format("COPY %s FROM STDIN WITH (", tablename));
			sb.append(String.format("  FORMAT %s", format));
			if ("TEXT".equalsIgnoreCase(format) || "CSV".equalsIgnoreCase(format)) {
				sb.append(String.format(", ENCODING '%s'", "UTF-8"));
				sb.append(String.format(", HEADER %b", header));
				sb.append(String.format(", DELIMITER E'%c'", delimiter));
				if ("CSV".equalsIgnoreCase(format)) {
					sb.append(String.format(", QUOTE E'%c'", quote));
				}
			}
			sb.append(");");
			
			final String sql = sb.toString();
			logger.debug(sql);
			
			// filePath指定が無い場合はエラーにする。
			if (filePath == null) {
				throw new IllegalArgumentException("File path must be specified for copyIn operation. Please use the file(String filePath) method to provide the file path.");
			}
			
			long copyInCount = 0;
			Connection connection = null;
			try {
				connection = DataSourceUtils.getConnection(dataSource);
				
				// データ投入
				try (final BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
					copyInCount = connection.unwrap(PGConnection.class)
							.getCopyAPI()
							.copyIn(sql, reader);
				}
				
				return copyInCount;
				
			} finally {
				DataSourceUtils.releaseConnection(connection, dataSource);
			}
		}
		
		private void setOption() {
			this.format = Objects.requireNonNullElse(this.format, optionFormat);
			this.header = Objects.requireNonNullElse(this.header, optionHeader);
			this.delimiter = Objects.requireNonNullElse(this.delimiter, optionDelimiter);
			this.quote = Objects.requireNonNullElse(this.quote, optionQuote);
		}

		private static boolean isTableName(String tablename) {
			return tablename.toLowerCase().matches("^([a-z_][a-z0-9_$]*)?\\.?([a-z_][a-z0-9_$]*)$");
		}
		
	}

}
