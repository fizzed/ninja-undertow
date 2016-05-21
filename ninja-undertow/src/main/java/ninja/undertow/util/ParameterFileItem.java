package ninja.undertow.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.util.FileItemHeadersImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ninja.uploads.FileItem;

/**
 * This {@link FileItem} type wraps a file received via a form parameter.
 * 
 * @author Jens Fendler <jf@jensfendler.com>
 *
 */
public class ParameterFileItem implements FileItem {

	private String filename;

	private FileItemHeaders headers;

	private File file;

	public ParameterFileItem() {
		headers = new FileItemHeadersImpl();
	}

	public ParameterFileItem(String filename, File file, FileItemHeaders headers) {
		this.filename = filename;
		this.file = file;
		this.headers = headers;
	}

	/**
	 * @see ninja.uploads.FileItem#getFileName()
	 */
	@Override
	public String getFileName() {
		return filename;
	}

	/**
	 * @see ninja.uploads.FileItem#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * @see ninja.uploads.FileItem#getFile()
	 */
	@Override
	public File getFile() {
		return file;
	}

	/**
	 * @see ninja.uploads.FileItem#getContentType()
	 */
	@Override
	public String getContentType() {
		return headers.getHeader("Content-Type");
	}

	/**
	 * @see ninja.uploads.FileItem#getHeaders()
	 */
	@Override
	@JsonIgnore
	public FileItemHeaders getHeaders() {
		return headers;
	}

	/**
	 * @see ninja.uploads.FileItem#cleanup()
	 */
	@Override
	public void cleanup() {
		// TODO check from where cleanup() is called and consider removing the
		// file.
	}

	@Override
	public String toString() {
		return "ParameterFileItem [filename=" + filename + ", file=" + file.getAbsolutePath() + "]";
	}
}
