package org.xli.model;

/**
 * 一个完整文件分割发送后的片段信息
 * 
 * @author xl
 *
 */
public class SetionFileInfo {
	private String fileName;
	private long length;
	private long offset;

	public long getLength() {
		return length;
	}
	
	public SetionFileInfo setLength(long length) {
		this.length = length;
		return this;
	}
	
	public long getOffset() {
		return offset;
	}
	
	public String getFileName() {
		return fileName;
	}

	public SetionFileInfo setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	public SetionFileInfo setOffSet(long offset) {
		this.offset = offset;
		return this;
	}

	@Override
	public String toString() {
		return "[fileName=" + fileName + ", length=" + length + ", offset=" + offset
				+ "]";
	}
}
