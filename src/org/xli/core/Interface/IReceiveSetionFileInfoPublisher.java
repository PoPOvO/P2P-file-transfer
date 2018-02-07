package org.xli.core.Interface;

import org.xli.model.SetionFileInfo;

/**
 * 抛出文件接收信息
 * 
 * @author xl
 *
 */
public interface IReceiveSetionFileInfoPublisher {
	void attachFileInfoListener(IReceiverSetionFileInfoListener listener);
	void removeFileInfoListener(IReceiverSetionFileInfoListener listener);
	void postFileInfo(SetionFileInfo setionFile);
}
