package com.tunm.gallerypro.data;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

/**
 * Created by dnld on 26/05/16.
 */
public class ContentHelper {

	private static final String TAG = "ContentHelper";

	/**
	 * Delete a file. May be even on external SD card.
	 *
	 * @param file the file to be deleted.
	 * @return True if successfully deleted.
	 */
	public static boolean deleteFile(Context context, @NonNull final File file) {

		// First try the normal deletion.
		boolean success = file.delete();

		// Try with Storage Access Framework.
//		if (!success && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			DocumentFile document = getDocumentFile(context, file, false, false);
//			success = document != null && document.delete();
//		}

		// Try the Kitkat workaround.
		if (!success && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			ContentResolver resolver = context.getContentResolver();

			try {
				Uri uri = null;//MediaStoreUtil.getUriFromFile(file.getAbsolutePath());
				if (uri != null) {
					resolver.delete(uri, null, null);
				}
				success = !file.exists();
			}
			catch (Exception e) {
				Log.e(TAG, "Error when deleting file " + file.getAbsolutePath(), e);
				return false;
			}
		}

		if(success) scanFile(context, new String[]{ file.getPath() });
		return success;
	}

	private static void scanFile(Context context, String[] paths) {
		MediaScannerConnection.scanFile(context, paths, null, null);
	}
}
