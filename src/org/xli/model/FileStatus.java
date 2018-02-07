package org.xli.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.xli.util.PropertiesReader;

/**
 * һ�������ļ���������Ϣ����
 * 
 * @author xl
 *
 */
public class FileStatus {
	private FileInfo fileInfo;
	
	private List<SetionFileInfo> receiveSetionFileList;
	
	public FileStatus() {
		//ʹ��ͬ��������װList�������̰߳�ȫ��
		this.receiveSetionFileList = Collections.synchronizedList(new LinkedList<>());
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public FileStatus setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
		return this;
	}

	/**
	 * ����Ƭ���ļ���Ϣ,ÿ����һ������һ�β�������ΪLinkedList�����̰߳�ȫ��Ҫ����
	 * 
	 * @param offset
	 * @param receiveSetionFileInfo
	 * @return
	 */
	public FileStatus addReceiveSetionFileInfo(SetionFileInfo receiveSetionFileInfo) {
		receiveSetionFileList.add(receiveSetionFileInfo);
		//����ƫ�����������У���Ϊsort()����ʹ��for eachѭ���������������ʹ��ͬ��������װList����Ȼ�޷���֤for each��׼ȷ��
		synchronized (receiveSetionFileList) {
			receiveSetionFileList.sort(new Comparator<SetionFileInfo>() {
				@Override
				public int compare(SetionFileInfo o1, SetionFileInfo o2) {
					return (int) (o1.getOffset() - o2.getOffset());
				}
			});
		}
		return this;
	}
	
	/**
	 * �ϲ������ļ������ŵ�src/mergeĿ¼��
	 */
	public void mergeFile() {
		File dir = new File("src/merge");
		String fileName = this.fileInfo.getTargetFileName();
		File endfile = new File(dir.getAbsolutePath() + File.separator + fileName);
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(endfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//����δ��Ƭ�ļ�
		if (this.receiveSetionFileList.size() == 1 
				&& this.fileInfo.getFileLen() == this.receiveSetionFileList.get(0).getLength()) {
			String setionFilePath = PropertiesReader.getProperty("receivedtempFileDir")
					+ receiveSetionFileList.get(0).getFileName();
			File tempFile = new File(setionFilePath);
			writeNewFile(tempFile, fos);
			
			return;
		}
		
		//�����Ƭ�ļ�
		for (int i = 0; i < receiveSetionFileList.size(); i++) {
			String setionFilePath = PropertiesReader.getProperty("receivedtempFileDir")
					+ receiveSetionFileList.get(i).getFileName();
			File tempFile = new File(setionFilePath);
			writeNewFile(tempFile, fos);
		}
		
		try {
			if (fos != null) {
				fos.close();
				fos = null;
			}
		} catch (IOException e1) {
		}
	}
	
	private void writeNewFile(File file, FileOutputStream fos) {
		FileInputStream fis = null;;
		
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		byte[] bytes = new byte[Integer.valueOf(PropertiesReader.getProperty("bufferSize"))];
		
		try {
			int readByteCount = 0;
			while ((readByteCount = fis.read(bytes, 0, bytes.length)) > 0) {
				fos.write(bytes, 0, readByteCount);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {
				}
			}
			
			file.delete();
		}
	}
	
	/**
	 * ��ȡ��ǰ���ڽ��յ���Ƭ���ļ�����
	 * 
	 * @return
	 */
	public int getReceivingSetionFileCount() {
		return receiveSetionFileList.size();
	}
	
	public FileStatus setReceiveSetionFileList(List<SetionFileInfo> list) {
		this.receiveSetionFileList = list;
		return this;
	}
}
