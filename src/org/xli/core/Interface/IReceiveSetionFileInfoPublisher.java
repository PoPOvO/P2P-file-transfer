package org.xli.core.Interface;

import org.xli.model.SetionFileInfo;

/**
 * �׳��ļ�������Ϣ
 * 
 * @author xl
 *
 */
public interface IReceiveSetionFileInfoPublisher {
	void attachFileInfoListener(IReceiverSetionFileInfoListener listener);
	void removeFileInfoListener(IReceiverSetionFileInfoListener listener);
	void postFileInfo(SetionFileInfo setionFile);
}
