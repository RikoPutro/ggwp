
package com.example.stegocipher.steganogarficipher;


public interface ProgressHandler {

	public void setTotal(int tot);
	public void increment(int inc);
	public void finished();
}
