package com.assassin.webviewlibrary.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.text.DecimalFormat;

/**
 * The helper class used to check the available space.
 * 
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Storage {
	// Keep reserved space is 16M to avoid download suspended.
	public static final long RESERVED_SPACE = 16 * 1024 * 1024;

	// apk size
	public static final long APK_SIZE = 8 * 1024 * 1024;

	/**
	 * Get the available space of the Android Download/Cache content directory.
	 * 
	 * @return available space
	 */
	public static long cachePartitionAvailableSpace() {
		StatFs stat = new StatFs(Environment.getDownloadCacheDirectory().getPath());
		long blockSize;
		long availableBlocks;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}

		return availableBlocks * blockSize;
	}

	/**
	 * Get the available space of the Android data directory.
	 * 
	 * @return available space
	 */
	public static long dataPartitionAvailableSpace() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize;
		long availableBlocks;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}

		return availableBlocks * blockSize;
	}

	/**
	 * To check whether the Android external storage directory is available or not.
	 * 
	 * @return
	 */
	public static boolean externalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/**
	 * Get the available space of the Android internal storage directory.
	 * 
	 * @return available space
	 */
	public static long internalStorageAvailableSpace() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize;
		long availableBlocks;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}

		return availableBlocks * blockSize;
	}

	/**
	 * Get the total space of the Android internal storage directory.
	 * 
	 * @return total space
	 */
	public static long internalStorageTotalSpace() {
		StatFs stat = new StatFs(Environment.getDataDirectory().getPath());

		long blockSize;
		long blockCount;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
			blockSize = stat.getBlockSizeLong();
			blockCount = stat.getBlockCountLong();
		} else {
			blockSize = stat.getBlockSize();
			blockCount = stat.getBlockCount();
		}

		return blockCount * blockSize;
	}

	/**
	 * Get the available space of the Android external storage directory.
	 * 
	 * @return available space
	 */
	public static long externalStorageAvailableSpace() {
		if (!externalStorageAvailable()) {
			return -1;
		}
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

		long blockSize;
		long availableBlocks;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
			blockSize = stat.getBlockSizeLong();
			availableBlocks = stat.getAvailableBlocksLong();
		} else {
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		}

		return availableBlocks * blockSize;
	}

	/**
	 * Get the total space of the Android external storage directory.
	 * 
	 * @return total space
	 */
	public static long externalStorageTotalSpace() {
		if (!externalStorageAvailable()) {
			return -1;
		}
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

		long blockSize;
		long blockCount;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
			blockSize = stat.getBlockSizeLong();
			blockCount = stat.getBlockCountLong();
		} else {
			blockSize = stat.getBlockSize();
			blockCount = stat.getBlockCount();
		}

		return blockCount * blockSize;
	}

	/**
	 * 
	 * @param size
	 *            The size to format.
	 * @return
	 */
	public static String readableSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.00").format(size / Math.pow(1024, digitGroups))
				+ units[digitGroups];
	}
}
