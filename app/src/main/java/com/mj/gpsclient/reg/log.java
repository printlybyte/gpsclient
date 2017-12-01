package com.mj.gpsclient.reg;

import java.io.File;
import java.io.FileOutputStream;

import android.os.Handler;
import android.text.format.Time;

/**
 * ��Ĺ���:
 * <p>
 * ��¼��־
 * 
 */
public class log {
	public static String exction;

	public log() {
		super();

	}

	/**
	 * ���ܣ���¼��־<br>
	 * 
	 * @param savePathStr
	 *            ������־·��
	 * @param saveFileNameS
	 *            ������־�ļ���
	 * @param saveDataStr
	 *            ������־���
	 * @param saveTypeStr
	 *            �������ͣ�falsΪ���Ǳ��棬trueΪ��ԭ���ļ�����ӱ���
	 */
	public static void recordLog(String savePathStr, String saveFileNameS,
			String saveDataStr, boolean saveTypeStr) {

	}

	public static void recordLog(String saveDataStr, boolean saveTypeStr) {

		String savePathStr = "/sdcard/singlepos";

		Time t = new Time();
		t.setToNow();
		String saveFileNameS = "iLogs";
		saveFileNameS = saveFileNameS + String.valueOf(t.year)
				+ String.valueOf(t.month + 1) + String.valueOf(t.monthDay);

		try {

			String savePath = savePathStr;
			String saveFileName = saveFileNameS;

			String saveData = String.valueOf(t.hour) + ":"
					+ String.valueOf(t.minute) + ":" + String.valueOf(t.second)
					+ "--->";
			saveData += saveDataStr;
			saveData += "\n";
			boolean saveType = saveTypeStr;

			// ׼����Ҫ������ļ�
			File saveFilePath = new File(savePath);
			if (!saveFilePath.exists()) {
				saveFilePath.mkdirs();
			}
			File saveFile = new File(savePath + "/" + saveFileName);
			if (!saveType && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();
				// �������ļ�
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && saveFile.exists()) {
				// saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && !saveFile.exists()) {
				saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void recordLog(Handler handler, String saveDataStr,
			boolean saveTypeStr) {
		// TODO Auto-generated method stub

		String savePathStr = "/sdcard/singlepos";

		Time t = new Time();
		t.setToNow();
		String saveFileNameS = "iLogs";
		saveFileNameS = saveFileNameS + String.valueOf(t.year)
				+ String.valueOf(t.month) + String.valueOf(t.monthDay);

		try {

			String savePath = savePathStr;
			String saveFileName = saveFileNameS;

			String saveData = "--->";
			saveData += saveDataStr;
			saveData += "\n";
			boolean saveType = saveTypeStr;

			// ׼����Ҫ������ļ�
			File saveFilePath = new File(savePath);
			if (!saveFilePath.exists()) {
				saveFilePath.mkdirs();
			}
			File saveFile = new File(savePath + "/" + saveFileName);
			if (!saveType && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();
				// �������ļ�
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && saveFile.exists()) {
				// saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && !saveFile.exists()) {
				saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}