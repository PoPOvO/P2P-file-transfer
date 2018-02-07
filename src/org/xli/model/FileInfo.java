package org.xli.model;

/**
 * 完整文件信息
 * 
 * @author xl
 *
 */
public class FileInfo {
	private String targetFileName;
	private long fileLen;

	public String getTargetFileName() {
		return targetFileName;
	}
	
	public FileInfo setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
		return this;
	}
	
	public long getFileLen() {
		return fileLen;
	}
	
	public FileInfo setFileLen(long fileLen) {
		this.fileLen = fileLen;
		return this;
	}
	
	@Override
	public String toString() {
		return "FileInfo [targetFileName=" + targetFileName + ", fileLen=" + fileLen + "]";
	}
}
